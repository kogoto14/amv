package dev.aulait.amv.domain.process;

import dev.aulait.amv.arch.util.JsonUtils;
import dev.aulait.amv.arch.util.ShortUuidUtils;
import dev.aulait.amv.domain.extractor.fdo.CrudPointFdo;
import dev.aulait.amv.domain.extractor.fdo.EntryPointFdo;
import dev.aulait.amv.domain.extractor.fdo.FieldFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.ParameterFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import dev.aulait.amv.domain.project.SourceFileEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProcessFactory {

  public SourceFileAggregate build(SourceFdo source) {
    SourceFileAggregate aggregate = new SourceFileAggregate();

    SourceFileEntity sourceFile = f2e(source);
    aggregate.setSourceFile(sourceFile);

    source.getTypes().stream()
        .map(this::f2e)
        .peek(type -> type.setSourceFile(sourceFile))
        .forEach(aggregate.getTypes()::add);

    return aggregate;
  }

  SourceFileEntity f2e(SourceFdo fdo) {
    SourceFileEntity entity = new SourceFileEntity();
    entity.setId(ShortUuidUtils.generate());
    entity.setPath(fdo.getPath());
    entity.setNamespace(fdo.getNamespace());
    return entity;
  }

  TypeEntity f2e(TypeFdo fdo) {
    TypeEntity entity = new TypeEntity();
    String typeId = ShortUuidUtils.generate();
    entity.setId(typeId);
    entity.setName(fdo.getName());
    // entity.setKind(fdo.getKind());
    entity.setQualifiedName(fdo.getQualifiedName());
    entity.setDataKind(fdo.getDataKind());
    entity.setDataName(fdo.getDataName());

    fdo.getFields().stream()
        .map(field -> map(field, typeId, entity.getFields().size() + 1))
        .forEach(entity.getFields()::add);

    fdo.getMethods().stream()
        .map(method -> f2e(method, typeId, entity.getMethods().size() + 1))
        .forEach(entity.getMethods()::add);

    if (!fdo.getAnnotations().isEmpty()) {
      entity.setAnnotations(JsonUtils.obj2json(fdo.getAnnotations()));
    }

    return entity;
  }

  FieldEntity map(FieldFdo fdo, String typeId, int seqNo) {
    FieldEntityId id = FieldEntityId.builder().typeId(typeId).seqNo(seqNo).build();

    FieldEntity entity = new FieldEntity();
    entity.setId(id);
    entity.setName(fdo.getName());
    entity.setType(fdo.getType());

    return entity;
  }

  MethodEntity f2e(MethodFdo fdo, String typeId, int seqNo) {
    MethodEntityId id = MethodEntityId.builder().typeId(typeId).seqNo(seqNo).build();

    MethodEntity entity = new MethodEntity();
    entity.setId(id);
    entity.setName(fdo.getName());
    entity.setQualifiedSignature(fdo.getQualifiedSignature());
    entity.setFallbackSignature(fdo.getFallbackSignature());
    entity.setInterfaceSignature(fdo.getInterfaceSignature());
    entity.setUnsolvedReason(fdo.getUnsolvedReason());
    entity.setLineNo(fdo.getLineNo());
    entity.setReturnType(fdo.getReturnType());

    fdo.getParameters().stream()
        .map(
            param ->
                f2e(param, typeId, entity.getId().getSeqNo(), entity.getMethodParams().size() + 1))
        .forEach(entity.getMethodParams()::add);

    collectMethodCall(fdo, entity);

    if (!fdo.getAnnotations().isEmpty()) {
      entity.setAnnotations(JsonUtils.obj2json(fdo.getAnnotations()));
    }

    if (fdo.getEntryPoint() != null) {
      EntryPointEntity entryPoint = f2e(entity.getId(), fdo.getEntryPoint());
      entity.setEntryPoint(entryPoint);
    }

    fdo.getCrudPoints().stream()
        .map(
            crud -> f2e(crud, typeId, entity.getId().getSeqNo(), entity.getCrudPoints().size() + 1))
        .forEach(entity.getCrudPoints()::add);

    return entity;
  }

  void collectMethodCall(MethodFdo methodF, MethodEntity methodE) {

    Map<MethodCallFdo, MethodCallEntity> fdo2entity = new HashMap<>();
    Map<String, MethodCallEntity> id2entity = new HashMap<>();

    for (MethodCallFdo methodCallF : methodF.getMethodCalls()) {
      MethodCallEntity methodCallE =
          f2e(
              methodCallF,
              methodE.getId().getTypeId(),
              methodE.getId().getSeqNo(),
              methodE.getMethodCalls().size() + 1);
      methodE.getMethodCalls().add(methodCallE);

      fdo2entity.put(methodCallF, methodCallE);
      id2entity.put(methodCallF.getId(), methodCallE);
    }

    for (Entry<MethodCallFdo, MethodCallEntity> entry : fdo2entity.entrySet()) {
      MethodCallFdo methodCallF = entry.getKey();
      MethodCallEntity methodCallE = entry.getValue();

      String callerId = methodCallF.getCallerId();
      if (callerId != null) {
        MethodCallEntity callerE = id2entity.get(callerId);
        methodCallE.setCallerSeqNo(callerE.getId().getSeqNo());
      }
    }
  }

  EntryPointEntity f2e(MethodEntityId id, EntryPointFdo fdo) {
    EntryPointEntity entity = new EntryPointEntity();
    entity.setId(
        EntryPointEntityId.builder().typeId(id.getTypeId()).methodSeqNo(id.getSeqNo()).build());
    entity.setPath(fdo.getPath());
    entity.setHttpMethod(fdo.getHttpMethod());
    return entity;
  }

  CrudPointEntity f2e(CrudPointFdo fdo, String typeId, int methodSeqNo, int seqNo) {
    CrudPointEntityId id =
        CrudPointEntityId.builder().typeId(typeId).methodSeqNo(methodSeqNo).seqNo(seqNo).build();

    CrudPointEntity entity = new CrudPointEntity();
    entity.setId(id);
    entity.setType(fdo.getType());
    entity.setDataName(fdo.getDataName());
    entity.setCrud(fdo.getCrud());
    entity.setKind(fdo.getKind());

    return entity;
  }

  MethodParamEntity f2e(ParameterFdo fdo, String typeId, int methodSeqNo, int seqNo) {
    MethodParamEntityId id =
        MethodParamEntityId.builder().typeId(typeId).methodSeqNo(methodSeqNo).seqNo(seqNo).build();

    MethodParamEntity entity = new MethodParamEntity();
    entity.setId(id);
    entity.setName(fdo.getName());
    entity.setType(fdo.getType());

    return entity;
  }

  MethodCallEntity f2e(MethodCallFdo fdo, String typeId, int methodSeqNo, int seqNo) {
    MethodCallEntityId id =
        MethodCallEntityId.builder().typeId(typeId).methodSeqNo(methodSeqNo).seqNo(seqNo).build();

    MethodCallEntity entity = new MethodCallEntity();
    entity.setId(id);
    entity.setQualifiedSignature(fdo.getQualifiedSignature());
    entity.setFallbackSignature(fdo.getFallbackSignature());
    entity.setInterfaceSignature(fdo.getInterfaceSignature());
    entity.setUnsolvedReason(fdo.getUnsolvedReason());
    entity.setLineNo(fdo.getLineNo());
    entity.setArgumentTypes(fdo.getArgumentTypes().stream().collect(Collectors.joining(",")));

    return entity;
  }
}
