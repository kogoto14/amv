package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.domain.extractor.fdo.CrudPointFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class JpaAdjuster implements MetadataAdjuster {

  @Override
  public List<SourceFdo> getAdditionals() {
    SourceFdo source = new SourceFdo();
    source.setPath(Path.of("jakarta/persistence/EntityManager.java").toString());
    source.setNamespace("jakarta.persistence");

    TypeFdo entityManager = new TypeFdo();
    entityManager.setName("EntityManager");
    entityManager.setQualifiedName("jakarta.persistence.EntityManager");
    source.getTypes().add(entityManager);

    MethodFdo persist = new MethodFdo();
    persist.setName("persist");
    persist.setQualifiedSignature("jakarta.persistence.EntityManager.persist(java.lang.Object)");
    entityManager.getMethods().add(persist);

    CrudPointFdo crudPoint = new CrudPointFdo();
    crudPoint.setKind("2");
    crudPoint.setCrud("C");
    persist.getCrudPoints().add(crudPoint);

    // TODO: add more CRUD methods

    return List.of(source);
  }

  @Override
  public void adjust(TypeFdo type) {
    type.getAnnotations()
        .forEach(
            annotation -> {
              if (StringUtils.equalsAny(
                  annotation.getQualifiedName(),
                  "javax.persistence.Table",
                  "jakarta.persistence.Table")) {

                type.setDataKind("D");
                type.setDataName(Objects.toString(annotation.getAttributes().get("name")));
              }
            });
  }
}
