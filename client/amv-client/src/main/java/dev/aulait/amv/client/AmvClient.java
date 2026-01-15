package dev.aulait.amv.client;

import dev.aulait.amv.async.AsyncProcessMonitor;
import dev.aulait.amv.browser.BrowserLauncher;
import dev.aulait.amv.browser.URLBuilder;
import dev.aulait.amv.http.CodebaseRegistryClient;
import java.net.http.HttpResponse;
import java.util.Optional;

public class AmvClient {

  private final AsyncProcessMonitor analysisMonitor;
  private final CodebaseRegistryClient registryClient;
  private final BrowserLauncher browserLauncher;
  private URLBuilder urlBuilder;

  private String apiBaseUrl = "http://localhost:8081";

  private String browserUrl = "http://localhost:5173";

  public AmvClient() {
    this.registryClient = new CodebaseRegistryClient(apiBaseUrl);
    this.analysisMonitor = AsyncProcessMonitor.withDefaults(registryClient::fetchAnalysisStatus);
    this.browserLauncher = new BrowserLauncher(browserUrl);
    this.urlBuilder = new URLBuilder(browserUrl);
  }

  public void setApiBaseUrl(String apiBaseUrl) {
    this.apiBaseUrl = apiBaseUrl;
    registryClient.setApiBaseUrl(apiBaseUrl);
  }

  public void setBrowserUrl(String browserUrl) {
    this.browserUrl = browserUrl;
    browserLauncher.setBrowserUrl(browserUrl);
    this.urlBuilder = new URLBuilder(browserUrl);
  }

  public static void main(String[] args) {
    AmvClient amvClient = new AmvClient();
    String response = amvClient.saveCodebase("MyCodebase");
    System.out.println(response);
  }

  public void visualize(String codebaseName, String qualifiedTypeName) {
    String codebaseId =
        findRegisteredCodebaseId(codebaseName).orElseGet(() -> saveCodebase(codebaseName));

    analyzeCodebase(codebaseId);

    analysisMonitor.waitUntilCompleted(codebaseId);

    String diagramUrl = urlBuilder.buildClassDiagramUrl(qualifiedTypeName);
    browserLauncher.open(diagramUrl);
  }

  private Optional<String> findRegisteredCodebaseId(String codebaseName) {
    HttpResponse<String> response = registryClient.getCodebase(codebaseName);
    String body = response.body();

    if (response.statusCode() == 204 && isNullLike(body)) {
      return Optional.empty();
    }

    if (response.statusCode() > 400) {
      throw new IllegalStateException(
          "Unexpected response from server: status=" + response.statusCode() + ", body=" + body);
    }

    return Optional.of(extractId(body));
  }

  private String saveCodebase(String codebaseName) {
    HttpResponse<String> response = registryClient.saveCodebase(codebaseName);
    if (response.statusCode() > 400) {
      throw new IllegalStateException(
          "Unexpected response from server: status="
              + response.statusCode()
              + ", body="
              + response.body());
    }
    return response.body();
  }

  private void analyzeCodebase(String codebaseId) {
    HttpResponse<String> response = registryClient.analyzeCodebase(codebaseId);
    if (response.statusCode() > 400) {
      throw new IllegalStateException(
          "Unexpected response from server: status="
              + response.statusCode()
              + ", body="
              + response.body());
    }
  }

  private boolean isNullLike(String s) {
    return s == null || s.isBlank() || "null".equalsIgnoreCase(s.trim());
  }

  private String extractId(String body) {
    java.util.regex.Matcher matcher =
        java.util.regex.Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
    if (matcher.find()) {
      return matcher.group(1);
    }
    throw new IllegalStateException("codebase id not found in body: " + body);
  }
}
