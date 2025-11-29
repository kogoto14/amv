package dev.aulait.amv.domain.process;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoadStatusService {

  public enum Status {
    RUNNING,
    SUCCESS,
  }

  private Status status = Status.RUNNING;

  public String getStatus() {
    return this.status.name();
  }

  public void running() {
    status = Status.RUNNING;
  }

  public void success() {
    status = Status.SUCCESS;
  }
}
