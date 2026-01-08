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
import lombok.Setter;

public class AmvClient {

  private HttpClient client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

  @Setter private String baseUrl = "http://localhost:8081";
  @Setter private String browserUrl = "http://localhost:5173";

  public static void main(String[] args) {
    AmvClient amvClient = new AmvClient();
    String response = amvClient.saveCodebase("MyCodebase");
    System.out.println(response);
  }

  public String saveCodebase(String codebaseName) {
    return post("codebases", "{\"name\":\"" + codebaseName + "\"}").body();
  }

  public String analyzeCodebase(String codebaseId) {
    return post("codebases/analyze/" + codebaseId, "").body();
  }

  public String saveAndAnalyzeCodebase(String codebaseName) {
    String codebaseId = saveCodebase(codebaseName);
    return analyzeCodebase(codebaseId);
  }

  public void visualize(String codebaseName, String qualifiedTypeName) {
    HttpResponse<String> response = get("codebases/by-name/" + codebaseName);
    String body = response.body();

    if (response.statusCode() == 404 || response.statusCode() == 204) {
      saveAndAnalyzeCodebase(codebaseName);
      return;
    }

    if (response.statusCode() != 200) {
      throw new IllegalStateException(
          "Unexpected response from server: status=" + response.statusCode() + ", body=" + body);
    }

    if (isNullLike(body)) {
      saveAndAnalyzeCodebase(codebaseName);
    } else {
      String id = extractId(body);
      analyzeCodebase(id);
    }

    // open();
  }

  private HttpRequest.Builder builer(String path) {
    return HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/api/" + path))
        .header("Content-Type", "application/json");
  }

  public HttpResponse<String> get(String path) {
    HttpRequest request = builer(path).GET().build();
    return send(request);
  }

  public HttpResponse<String> post(String path, String body) {
    HttpRequest request = builer(path).POST(HttpRequest.BodyPublishers.ofString(body)).build();
    return send(request);
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
