package dev.aulait.amv.domain.process;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class SourceFileService {

  private final EntityManager em;
  private final CrudPointRepository crudPointRepository;
  private final FieldRepository fieldRepository;
  private final MethodParamRepository methodParamRepository;

  @Transactional
  public void save(SourceFileAggregate sourceFile) {
    em.persist(sourceFile.getSourceFile());
    sourceFile.getTypes().forEach(this::saveDeclaration);

    if (sourceFile.getFlowStatements() != null) {
      sourceFile.getFlowStatements().forEach(this::saveDeclaration);
    }
  }

  @Transactional
  public int saveDeclaration(TypeEntity type) {
    em.persist(type);

    fieldRepository.saveAll(type.getFields());

    type.getMethods().stream().forEach(this::saveDeclaration);

    return 1;
  }

  @Transactional
  public int saveDeclaration(MethodEntity method) {
    em.persist(method);

    if (method.getEntryPoint() != null) {
      em.persist(method.getEntryPoint());
    }

    methodParamRepository.saveAll(method.getMethodParams());

    method.getMethodCalls().stream()
        .sorted(Comparator.comparing(mc -> mc.getId().getSeqNo()))
        .forEach(
            mc -> {
              em.persist(mc);
            });

    crudPointRepository.saveAll(method.getCrudPoints());

    return 1;
  }

  public int saveDeclaration(FlowStatementEntity flowStatement) {
    if (flowStatement == null) {
      return 0;
    }

    if (flowStatement.getFlowStatement() != null) {
      saveDeclaration(flowStatement.getFlowStatement());
    }
    em.persist(flowStatement);
    return 1;
  }
}
