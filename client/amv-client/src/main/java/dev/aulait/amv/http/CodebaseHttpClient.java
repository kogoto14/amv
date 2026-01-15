package dev.aulait.amv.http;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;

public class CodebaseHttpClient extends BaseHttpClient {

  public CodebaseHttpClient(String apiBaseUrl) {
    super(apiBaseUrl);
  }

  CodebaseHttpClient(HttpClient httpClient, String apiBaseUrl) {
    super(httpClient, apiBaseUrl);
  }

  public void setApiBaseUrl(String apiBaseUrl) {
    setBaseUrl(apiBaseUrl);
  }

  public Optional<String> findRegisteredCodebaseId(String codebaseName) {
    HttpResponse<String> response = getCodebase(codebaseName);
    String body = response.body();

    if (response.statusCode() == 204 && isNullLike(body)) {
      return Optional.empty();
    }

    if (!isSuccessStatusCode(response.statusCode())) {
      throwUnexpectedResponse(response);
    }

    return Optional.of(extractId(body));
  }

  public String saveCodebase(String codebaseName) {
    HttpResponse<String> response = save(codebaseName);

    if (!isSuccessStatusCode(response.statusCode())) {
      throwUnexpectedResponse(response);
    }

    return response.body();
  }

  public void analyzeCodebase(String codebaseId) {
    HttpResponse<String> response = analyze(codebaseId);

    if (!isSuccessStatusCode(response.statusCode())) {
      throwUnexpectedResponse(response);
    }
  }

  public HttpResponse<String> fetchAnalysisStatus(String codebaseId) {
    HttpResponse<String> response = fetchAsyncStatus(codebaseId);

    if (!isSuccessStatusCode(response.statusCode()) && response.statusCode() != 404) {
      // 404 is expected when the async process has not started yet.
      throwUnexpectedResponse(response);
    }

    return response;
  }

  private HttpResponse<String> getCodebase(String codebaseName) {
    return get("codebases/by-name/" + codebaseName);
  }

  private HttpResponse<String> save(String codebaseName) {
    return post("codebases", "{\"name\":\"" + codebaseName + "\"}");
  }

  private HttpResponse<String> analyze(String codebaseId) {
    return post("codebases/analyze/" + codebaseId, "");
  }

  private HttpResponse<String> fetchAsyncStatus(String codebaseId) {
    return get("async/" + codebaseId);
  }

  private void throwUnexpectedResponse(HttpResponse<String> response) {
    throw new IllegalStateException(
        "Unexpected response from server: status="
            + response.statusCode()
            + ", body="
            + response.body());
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

  private boolean isSuccessStatusCode(int statusCode) {
    return statusCode >= 200 && statusCode < 300;
  }
}
