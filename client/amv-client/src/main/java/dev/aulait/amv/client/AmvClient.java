package dev.aulait.amv.client;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.Setter;

public class AmvClient {

  private static final Duration ANALYSIS_TIMEOUT = Duration.ofMinutes(15);
  private static final Duration ANALYSIS_POLL_INTERVAL = Duration.ofSeconds(3);

  private HttpClient client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

  @Setter private String apiBaseUrl = "http://localhost:8081";

  @Setter private String browserUrl = "http://localhost:5173";

  private enum AnalysisStatus {
    RUNNING,
    COMPLETED,
    FAILED,
    UNKNOWN;

    static AnalysisStatus from(String raw) {
      if (raw == null || raw.isBlank()) {
        return UNKNOWN;
      }
      try {
        return AnalysisStatus.valueOf(raw.trim().toUpperCase());
      } catch (IllegalArgumentException ex) {
        return UNKNOWN;
      }
    }

    boolean isRunning() {
      return this == RUNNING;
    }

    boolean isSuccessful() {
      return this == COMPLETED;
    }

    boolean isFailure() {
      return this == FAILED || this == UNKNOWN;
    }
  }

  public static void main(String[] args) {
    AmvClient amvClient = new AmvClient();
    String response = amvClient.saveCodebase("MyCodebase");
    System.out.println(response);
  }

  public void visualize(String codebaseName, String qualifiedTypeName) {
    findRegisteredCodebaseId(codebaseName)
        .ifPresentOrElse(this::analyzeCodebase, () -> saveAndAnalyzeCodebase(codebaseName));

    // open();
  }

  private Optional<String> findRegisteredCodebaseId(String codebaseName) {
    HttpResponse<String> response = get("codebases/by-name/" + codebaseName);
    String body = response.body();

    if (response.statusCode() == 204 && isNullLike(body)) {
      return Optional.empty();
    }

    if (response.statusCode() != 200) {
      throw new IllegalStateException(
          "Unexpected response from server: status=" + response.statusCode() + ", body=" + body);
    }

    return Optional.of(extractId(body));
  }

  private String saveAndAnalyzeCodebase(String codebaseName) {
    String codebaseId = saveCodebase(codebaseName);
    return analyzeCodebase(codebaseId);
  }

  private String saveCodebase(String codebaseName) {
    return post("codebases", "{\"name\":\"" + codebaseName + "\"}").body();
  }

  private String analyzeCodebase(String codebaseId) {
    HttpResponse<String> response = post("codebases/analyze/" + codebaseId, "");
    if (response.statusCode() >= 400) {
      throw new IllegalStateException(
          "Failed to start analysis: status="
              + response.statusCode()
              + ", body="
              + response.body());
    }

    waitForAnalysisCompletion(codebaseId);
    return response.body();
  }

  private void waitForAnalysisCompletion(String codebaseId) {
    Instant deadline = Instant.now().plus(ANALYSIS_TIMEOUT);

    while (Instant.now().isBefore(deadline)) {
      HttpResponse<String> statusResponse = get("async/" + codebaseId);
      int statusCode = statusResponse.statusCode();

      if (statusCode == 200) {
        AnalysisStatus status = AnalysisStatus.from(statusResponse.body());
        if (status.isRunning()) {
          sleepUntilNextPoll();
          continue;
        }
        if (status.isSuccessful()) {
          return;
        }
        if (status.isFailure()) {
          throw new IllegalStateException(
              "Codebase analysis failed: id=" + codebaseId + ", status=" + status);
        }
      } else if (statusCode != 404) {
        throw new IllegalStateException(
            "Unexpected async status response: status="
                + statusCode
                + ", body="
                + statusResponse.body());
      }
      sleepUntilNextPoll();
    }

    throw new IllegalStateException(
        "Timed out waiting for codebase analysis to finish: id=" + codebaseId);
  }

  private void sleepUntilNextPoll() {
    try {
      Thread.sleep(ANALYSIS_POLL_INTERVAL.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting for analysis", e);
    }
  }

  private boolean isNullLike(String s) {
    return s == null || s.isBlank() || "null".equalsIgnoreCase(s.trim());
  }

  private String extractId(String body) {
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
    if (m.find()) {
      return m.group(1);
    }
    throw new IllegalStateException("codebase id not found in body: " + body);
  }

  private HttpResponse<String> get(String path) {
    HttpRequest request = builer(path).GET().build();
    return send(request);
  }

  private HttpResponse<String> post(String path, String body) {
    HttpRequest request = builer(path).POST(HttpRequest.BodyPublishers.ofString(body)).build();
    return send(request);
  }

  private HttpRequest.Builder builer(String path) {
    return HttpRequest.newBuilder()
        .uri(URI.create(apiBaseUrl + "/api/" + path))
        .header("Content-Type", "application/json");
  }

  private HttpResponse<String> send(HttpRequest request) {
    try {
      System.out.println("AMV client: " + request.method() + " " + request.uri());
      HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println(
          "AMV client: response status="
              + resp.statusCode()
              + ", bodySnippet="
              + (resp.body() == null
                  ? "null"
                  : resp.body().substring(0, Math.min(200, resp.body().length()))));
      return resp;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  private void open() {
    if (isDevContainer()) {
      exec("code", "--openExternal", browserUrl);
      return;
    }
    try {
      Desktop.getDesktop().browse(new URI(browserUrl));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private boolean isDevContainer() {
    return "true".equals(System.getenv("REMOTE_CONTAINERS"))
        || "true".equals(System.getenv("CODESPACES"));
  }

  private int exec(String... command) {
    ProcessBuilder processBuilder = new ProcessBuilder(command);

    try {
      Process process = processBuilder.start();

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        reader.lines().forEach(System.out::println);
      }

      return process.waitFor();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }
}
