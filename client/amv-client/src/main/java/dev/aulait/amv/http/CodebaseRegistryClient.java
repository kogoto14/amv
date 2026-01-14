package dev.aulait.amv.http;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class CodebaseRegistryClient extends BaseHttpClient {

  public CodebaseRegistryClient(String apiBaseUrl) {
    super(apiBaseUrl);
  }

  CodebaseRegistryClient(HttpClient httpClient, String apiBaseUrl) {
    super(httpClient, apiBaseUrl);
  }

  public void setApiBaseUrl(String apiBaseUrl) {
    setBaseUrl(apiBaseUrl);
  }

  public HttpResponse<String> getCodebase(String codebaseName) {
    return get("codebases/by-name/" + codebaseName);
  }

  public HttpResponse<String> saveCodebase(String codebaseName) {
    return post("codebases", "{\"name\":\"" + codebaseName + "\"}");
  }

  public HttpResponse<String> analyzeCodebase(String codebaseId) {
    return post("codebases/analyze/" + codebaseId, "");
  }

  public HttpResponse<String> fetchAnalysisStatus(String codebaseId) {
    return get("async/" + codebaseId);
  }
}
