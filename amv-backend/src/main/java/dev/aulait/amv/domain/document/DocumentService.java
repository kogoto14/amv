package dev.aulait.amv.domain.document;

import dev.aulait.amv.domain.diagram.DiagramService;
import dev.aulait.amv.domain.diagram.DiagramVo;
import dev.aulait.amv.domain.diagram.SequenceDiagramVo;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class DocumentService {

  private final DiagramService diagramService;

  private final MethodRepository methodRepository;

  public InteractionDocumentVo generateInteractionDocument(
      String qualifiedSignature, List<String> participableStereotypes) {
    Optional<MethodEntity> methodOpt =
        methodRepository.findByQualifiedSignature(qualifiedSignature);

    SequenceDiagramVo sequenceDiagram =
        diagramService.generateSequenceDiagram(methodOpt, participableStereotypes);

    DiagramVo classDiagram =
        diagramService.generateClassDiagram(sequenceDiagram.getParamOrReturnTypes());

    return InteractionDocumentVo.builder()
        .sequenceDiagram(sequenceDiagram)
        .classDiagram(classDiagram)
        .build();
  }
}
