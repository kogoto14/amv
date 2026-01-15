package dev.aulait.amv.async;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncProcessMonitor {

  private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(15);
  private static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(3);

  private final Duration timeout;
  private final Duration pollInterval;
  private final Function<String, HttpResponse<String>> statusFetcher;

  private final Logger logger = Logger.getLogger(AsyncProcessMonitor.class.getName());

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
      AsyncProcessStatus status = AsyncProcessStatus.from(statusResponse.body());

      if (status.isRunning()) {
        logger.log(
            Level.INFO,
            "Async process is still running: id={0}, status={1}",
            new Object[] {execId, status});
        sleepUntilNextPoll();
        continue;
      }
      if (status.isSuccessed()) {
        logger.log(
            Level.INFO,
            "Async process completed successfully: id={0}, status={1}",
            new Object[] {execId, status});
        return;
      }
      if (statusCode != 404 && status.isFailed()) {
        throw new IllegalStateException(
            "Async process failed: id=" + execId + ", status=" + status);
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
