package dev.aulait.amv.arch.async;

import dev.aulait.amv.arch.util.ShortUuidUtils;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Asynchronous;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class AsyncExecService {

  private final AsyncExecStatusStore statusStore;

  public String runAsync(Runnable runnable) {
    String execId = ShortUuidUtils.generate();
    runAsync(execId, runnable);
    return execId;
  }

  @Asynchronous
  public CompletionStage<Void> runAsync(String execId, Runnable runnable) {
    log.info("Starting asynchronous execution: {}", execId);

    return CompletableFuture.runAsync(
            () -> {
              statusStore.setStatus(execId, AsyncExecStatus.RUNNING);
              runnable.run();
              statusStore.setStatus(execId, AsyncExecStatus.COMPLETED);
            })
        .exceptionally(
            (e) -> {
              log.error("Asynchronous execution failed: {}", execId, e);
              statusStore.setStatus(execId, AsyncExecStatus.FAILED);
              return null;
            });
  }

  public AsyncExecStatus getStatus(String execId) {
    return statusStore.getStatus(execId);
  }

  public boolean isRunning(String execId) {
    AsyncExecStatus status = statusStore.getStatus(execId);
    return status == null ? false : !status.isFinished();
  }

  public boolean registerCallback(String execId, Consumer<AsyncExecNotificationDto> callback) {
    return statusStore.registerCallback(execId, callback);
  }

  public void notify(String execId, String message) {
    statusStore.notify(execId, message);
  }
}
