package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.MethodCallRepository;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodRepository;
import dev.aulait.amv.domain.process.TypeEntity;
import dev.aulait.amv.domain.process.TypeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class DiagramService {

  private final MethodRepository methodRepository;
  private final MethodCallRepository methodCallRepository;
  private final TypeRepository typeRepository;

  public List<CrudElementVo> getCrudDiagram() {
    Pageable pageable = Pageable.ofSize(100);
    List<MethodEntity> methods = methodRepository.findByEntryPointIsNotNull(pageable);

    log.debug("Found {} entry point methods.", methods.size());

    if (methods.isEmpty()) {
      return List.of();
    }

    CrudDiagramLogic logic = new CrudDiagramLogic(typeRepository::findByQualifiedName);
    List<CrudElementVo> crudElements = logic.getCrudDiagram(methods);

    return crudElements;
  }

  public SequenceDiagramVo generateSequenceDiagram(
      Optional<MethodEntity> methodOpt, List<String> participableStereotypes) {

    if (methodOpt.isEmpty()) {
      return SequenceDiagramVo.empty();
    }

    SequenceDiagramLogic2 sequenceDiagramLogic =
        new SequenceDiagramLogic2(typeRepository::findById, methodCallRepository::findByCallerId);

    SequenceDiagramVo diagram =
        sequenceDiagramLogic.generate(methodOpt.get(), participableStereotypes);

    return diagram;
  }

  public DiagramVo generateClassDiagram(Collection<String> qualifiedNames) {
    List<TypeEntity> types = typeRepository.findByQualifiedNameIn(qualifiedNames);

    if (types.isEmpty()) {
      // TODO: return empty diagram string
      return DiagramVo.empty();
    }

    ClassDiagramLogic classDiagramLogic =
        new ClassDiagramLogic(typeRepository::findByQualifiedName);
    DiagramVo diagram = classDiagramLogic.generateClassDiagram(types);

    return diagram;
  }
}
