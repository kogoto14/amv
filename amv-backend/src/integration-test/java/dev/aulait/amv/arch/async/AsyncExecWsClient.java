package dev.aulait.amv.arch.async;

import dev.aulait.amv.arch.test.TestConfig;
import dev.aulait.amv.arch.util.JsonUtils;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;

public class AsyncExecWsClient {

  public AsyncExecStatus waitUntilFinished(String execId) {
    Endpoint endpoint = new Endpoint();
    try (Session session =
        ContainerProvider.getWebSocketContainer().connectToServer(endpoint, getUri(execId))) {

      Awaitility.await().atMost(Duration.ofSeconds(120)).until(() -> endpoint.status.isFinished());

      return endpoint.status;
    } catch (DeploymentException | IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  URI getUri(String execId) {
    return URI.create(TestConfig.getInstance().getWsBaseUrl() + "chat/" + execId);
  }

  @ClientEndpoint
  @Slf4j
  public static class Endpoint {
    private AsyncExecStatus status = AsyncExecStatus.RUNNING;

    @OnOpen
    public void open(Session session) {
      log.info("WebSocket opened: {}", session.getId());
    }

    @OnMessage
    void message(String msg) {
      log.info("Received message: {}", msg);
      AsyncExecNotificationDto notification =
          JsonUtils.str2obj(msg, AsyncExecNotificationDto.class);
      status = notification.getStatus();
    }

    @OnError
    public void error(Session session, Throwable throwable) {
      log.error("WebSocket error on session {}: {}", session.getId(), throwable.getMessage());
      status = AsyncExecStatus.FAILED;
    }
  }
}
