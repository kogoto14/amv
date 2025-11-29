package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.MethodCallEntity;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodEntityDummy;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CallTreeElementVo {
  /** true: call, false: called */
  private boolean call;

  private int depth;
  private MethodEntity method;
  private int lineNoAt;
  private String typeIdAt;

  public static CallTreeElementVo callTree(
      int depth, MethodEntity method, String calledTypeId, int calledLineNo) {
    return CallTreeElementVo.builder()
        .call(true)
        .depth(depth)
        .method(method)
        .lineNoAt(calledLineNo)
        .typeIdAt(calledTypeId)
        .build();
  }

  public static CallTreeElementVo callTree(
      int depth, MethodCallEntity methodCall, String callerTypeId) {
    return CallTreeElementVo.builder()
        .call(true)
        .depth(depth)
        .method(MethodEntityDummy.of(methodCall))
        .lineNoAt(methodCall.getLineNo())
        .typeIdAt(callerTypeId)
        .build();
  }

  public static CallTreeElementVo calledTree(int depth, MethodEntity method, int callLineNo) {
    return CallTreeElementVo.builder()
        .call(false)
        .depth(depth)
        .method(method)
        .lineNoAt(callLineNo)
        .typeIdAt(method.getId().getTypeId())
        .build();
  }
}
