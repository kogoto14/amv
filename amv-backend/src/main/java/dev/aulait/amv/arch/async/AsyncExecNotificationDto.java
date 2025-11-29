package dev.aulait.amv.arch.async;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class AsyncExecNotificationDto {
  private AsyncExecStatus status;
  private String message;

  public boolean isFinished() {
    return status == null ? true : status.isFinished();
  }
}
