package dev.aulait.amv.interfaces.document;

import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.domain.document.DocumentService;
import dev.aulait.amv.domain.document.InteractionDocumentVo;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@Path(DocumentController.BASE_PATH)
@RequiredArgsConstructor
public class DocumentController {

  static final String BASE_PATH = "/documents";

  static final String INTERACTION_PATH = "/interaction";

  private final DocumentService service;

  @POST
  @Path(INTERACTION_PATH)
  public InteractionResponseDto getInteractionDocument(InteractionDocumentCriteriaDto criteria) {
    InteractionDocumentVo vo =
        service.generateInteractionDocument(
            criteria.getQualifiedSignature(), criteria.getParticipableStereotypes());
    return BeanUtils.map(vo, InteractionResponseDto.class);
  }
}
