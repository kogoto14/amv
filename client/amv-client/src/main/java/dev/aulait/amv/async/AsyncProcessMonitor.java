package dev.aulait.amv.client.async;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

public class AsyncProcessMonitor {

  private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(15);
  private static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(3);

  private final Duration timeout;
  private final Duration pollInterval;
  private final Function<String, HttpResponse<String>> statusFetcher;

  private AsyncProcessMonitor(
      Duration timeout,
      Duration pollInterval,
      Function<String, HttpResponse<String>> statusFetcher) {
    this.timeout = Objects.requireNonNull(timeout, "timeout");
    this.pollInterval = Objects.requireNonNull(pollInterval, "pollInterval");
    this.statusFetcher = Objects.requireNonNull(statusFetcher, "statusFetcher");
  }

  public static AsyncProcessMonitor withDefaults(
      Function<String, HttpResponse<String>> statusFetcher) {
    return new AsyncProcessMonitor(DEFAULT_TIMEOUT, DEFAULT_POLL_INTERVAL, statusFetcher);
  }

  public void waitUntilCompleted(String execId) {
    Instant deadline = Instant.now().plus(timeout);

    while (Instant.now().isBefore(deadline)) {
      HttpResponse<String> statusResponse = statusFetcher.apply(execId);
      int statusCode = statusResponse.statusCode();

      if (statusCode == 200) {
        AsyncProcessStatus status = AsyncProcessStatus.from(statusResponse.body());
        if (status.isRunning()) {
          System.out.println("Async process is still running...");
          sleepUntilNextPoll();
          continue;
        }
        if (status.isSuccessed()) {
          System.out.println("Async process completed successfully.");
          return;
        }
        if (status.isFailed()) {
          throw new IllegalStateException(
              "Async process failed: id=" + execId + ", status=" + status);
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

    throw new IllegalStateException("Timed out waiting for async process to finish: id=" + execId);
  }

  private void sleepUntilNextPoll() {
    try {
      Thread.sleep(pollInterval.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting for async process", e);
    }
  }
}
