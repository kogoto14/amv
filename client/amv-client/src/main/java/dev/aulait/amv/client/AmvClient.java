package dev.aulait.amv.client;

import dev.aulait.amv.async.AsyncProcessMonitor;
import dev.aulait.amv.browser.BrowserLauncher;
import dev.aulait.amv.browser.URLBuilder;
import dev.aulait.amv.http.CodebaseHttpClient;
import java.util.Objects;

public class AmvClient {

  private static final String DEFAULT_API_BASE_URL = "http://localhost:8081";
  private static final String DEFAULT_BROWSER_BASE_URL = "http://localhost:5173";

  private final AsyncProcessMonitor analysisMonitor;
  private final CodebaseHttpClient codebaseHttpClient;
  private final BrowserLauncher browserLauncher;
  private final URLBuilder urlBuilder;

  public AmvClient(
      CodebaseHttpClient codebaseHttpClient,
      AsyncProcessMonitor analysisMonitor,
      BrowserLauncher browserLauncher,
      URLBuilder urlBuilder,
      String apiBaseUrl,
      String browserUrl) {
    this.codebaseHttpClient = Objects.requireNonNull(codebaseHttpClient, "codebaseHttpClient");
    this.analysisMonitor = Objects.requireNonNull(analysisMonitor, "analysisMonitor");
    this.browserLauncher = Objects.requireNonNull(browserLauncher, "browserLauncher");
    this.urlBuilder = Objects.requireNonNull(urlBuilder, "urlBuilder");
    setApiBaseUrl(apiBaseUrl);
    setBrowserUrl(browserUrl);
  }

  public static AmvClient createDefault() {
    CodebaseHttpClient codebaseHttpClient = new CodebaseHttpClient(DEFAULT_API_BASE_URL);
    AsyncProcessMonitor monitor =
        AsyncProcessMonitor.withDefaults(codebaseHttpClient::fetchAnalysisStatus);
    BrowserLauncher launcher = new BrowserLauncher();
    URLBuilder builder = new URLBuilder(DEFAULT_BROWSER_BASE_URL);
    return new AmvClient(
        codebaseHttpClient,
        monitor,
        launcher,
        builder,
        DEFAULT_API_BASE_URL,
        DEFAULT_BROWSER_BASE_URL);
  }

  public void setApiBaseUrl(String apiBaseUrl) {
    String value = Objects.requireNonNull(apiBaseUrl, "apiBaseUrl");
    codebaseHttpClient.setApiBaseUrl(value);
  }

  public void setBrowserUrl(String browserUrl) {
    String value = Objects.requireNonNull(browserUrl, "browserUrl");
    urlBuilder.setBrowserBaseUrl(value);
  }

  public static void main(String[] args) {
    AmvClient amvClient = AmvClient.createDefault();
    String response = amvClient.codebaseHttpClient.saveCodebase("MyCodebase");
    System.out.println(response);
  }

  public void visualize(String codebaseName, String qualifiedTypeName) {
    String codebaseId =
        codebaseHttpClient
            .findRegisteredCodebaseId(codebaseName)
            .orElseGet(() -> codebaseHttpClient.saveCodebase(codebaseName));

    codebaseHttpClient.analyzeCodebase(codebaseId);

    analysisMonitor.waitUntilCompleted(codebaseId);

    String diagramUrl = urlBuilder.buildClassDiagramUrl(qualifiedTypeName);
    browserLauncher.open(diagramUrl);
  }
}
