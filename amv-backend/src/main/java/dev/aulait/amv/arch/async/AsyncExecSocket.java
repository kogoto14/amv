package dev.aulait.amv.arch.async;

import dev.aulait.amv.arch.util.JsonUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@ServerEndpoint("/chat/{execId}")
@Slf4j
public class AsyncExecSocket {

  private final AsyncExecService service;

  @OnOpen
  public void onOpen(Session session, @PathParam("execId") String execId) {
    boolean registered =
        service.registerCallback(
            execId, notification -> sendMessage(session, execId, notification));

    if (!registered) {
      sendMessage(session, execId, AsyncExecStatus.UNKNOWN);
    }
  }

  void sendMessage(Session session, String execId, AsyncExecStatus status) {
    sendMessage(session, execId, AsyncExecNotificationDto.builder().status(status).build());
  }

  void sendMessage(Session session, String execId, AsyncExecNotificationDto notification) {
    session
        .getAsyncRemote()
        .sendText(
            JsonUtils.obj2json(notification),
            result -> {
              if (result.getException() != null) {
                log.error("Failed to send message for execId: " + execId, result.getException());
              }
            });

    if (notification.isFinished()) {
      try {
        session.close();
      } catch (IOException e) {
        log.error("Failed to close session for execId: " + execId, e);
      }
    }
  }
}
