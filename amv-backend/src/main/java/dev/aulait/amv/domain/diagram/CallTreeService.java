package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodRepository;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@ApplicationScoped
@RequiredArgsConstructor
public class CallTreeService {

  private final MethodRepository methodRepository;
  private final CallTreeLogic callTreeLogic;

  @PostConstruct
  public void init() {
    callTreeLogic.setFindMethodCallByCalleeId(methodRepository::findByCalleeId);
  }

  public List<CallTreeVo> buildCallTree(
      String signaturePattern, boolean callTreeRequired, boolean calledTreeRequired, int limit) {

    List<MethodEntity> methods =
        methodRepository.findByQualifiedSignatureLike(
            "%" + signaturePattern + "%", Pageable.ofSize(limit));

    List<CallTreeVo> callTrees = methods.stream().map(CallTreeVo::new).toList();

    if (callTreeRequired) {
      callTrees.forEach(
          ct -> ct.getCallTrees().addAll(callTreeLogic.buildCallTree(ct.getMethod())));
    }
    if (calledTreeRequired) {
      callTrees.forEach(
          ct -> ct.getCalledTrees().addAll(callTreeLogic.buildCalledTree(ct.getMethod())));
    }

    return callTrees;
  }
}
