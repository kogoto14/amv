package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.MethodEntity;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CrudElementVo {
  private String entryPoint;
  private MethodEntity method;
  private String table;
  private String crud;
}
