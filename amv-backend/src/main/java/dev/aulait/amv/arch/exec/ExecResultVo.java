package dev.aulait.amv.arch.exec;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@RequiredArgsConstructor
@Value
public class ExecResultVo {
  private final int exitCode;
  private final String stdout;
  private final String stderr;
}
