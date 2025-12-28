package dev.aulait.amv.client;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.Setter;

public class AmvClient {

  private HttpClient client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

  @Setter private String baseUrl = "http://localhost:8081";

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
      return client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }
}
