package dev.aulait.amv.arch.async;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationScoped
public class AsyncExecStatusStore {

  private Map<String, AsyncExecStatus> store = new ConcurrentHashMap<>();
  private Map<String, Consumer<AsyncExecNotificationDto>> callbacks = new ConcurrentHashMap<>();

  public void setStatus(String key, AsyncExecStatus status) {
    store.put(key, status);

    notify(key, status, null);
  }

  public boolean registerCallback(String key, Consumer<AsyncExecNotificationDto> callback) {
    if (!store.containsKey(key)) {
      return false;
    }
    callbacks.put(key, callback);
    return true;
  }

  public AsyncExecStatus getStatus(String key) {
    return store.get(key);
  }

  public void notify(String key, String message) {
    notify(key, getStatus(key), message);
  }

  public void notify(String key, AsyncExecStatus status, String message) {

    Consumer<AsyncExecNotificationDto> callback = callbacks.get(key);
    if (callback != null) {
      callback.accept(AsyncExecNotificationDto.builder().status(status).message(message).build());
    }
  }
}
