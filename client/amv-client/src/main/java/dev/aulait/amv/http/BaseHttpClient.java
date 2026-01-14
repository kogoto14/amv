package dev.aulait.amv.http;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

abstract class BaseHttpClient {

  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

  private final HttpClient httpClient;
  private String apiBaseUrl;

  protected BaseHttpClient(String apiBaseUrl) {
    this(HttpClient.newBuilder().connectTimeout(DEFAULT_TIMEOUT).build(), apiBaseUrl);
  }

  protected BaseHttpClient(HttpClient httpClient, String apiBaseUrl) {
    this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
    this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl, "apiBaseUrl");
  }

  protected void setBaseUrl(String apiBaseUrl) {
    this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl, "apiBaseUrl");
  }

  protected HttpResponse<String> get(String path) {
    HttpRequest request = requestBuilder(path).GET().build();
    return send(request);
  }

  protected HttpResponse<String> post(String path, String body) {
    HttpRequest request =
        requestBuilder(path).POST(HttpRequest.BodyPublishers.ofString(body)).build();
    return send(request);
  }

  protected HttpRequest.Builder requestBuilder(String path) {
    return HttpRequest.newBuilder().uri(buildUri(path)).header("Content-Type", "application/json");
  }

  private URI buildUri(String path) {
    return URI.create(apiBaseUrl + "/api/" + path);
  }

  private HttpResponse<String> send(HttpRequest request) {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }
}
