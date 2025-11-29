package dev.aulait.amv.domain.extractor.mybatis;

import dev.aulait.amv.domain.extractor.crud.CrudExtractor;
import dev.aulait.amv.domain.extractor.fdo.AnnotationFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import dev.aulait.amv.domain.extractor.java.MetadataAdjuster;
import java.nio.file.Path;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class MyBatisAdjuster implements MetadataAdjuster {

  /** Key: method signature (name.space.TypeName.methodName), Value: SQL statement */
  private Properties mapperSqls = new Properties();

  CrudExtractor crudExtractor = new CrudExtractor();

  MyBatisExtractor extractor = new MyBatisExtractor();

  @Override
  public void init(Path projectDir, String classpath) {
    mapperSqls = extractor.extract(projectDir, classpath);
  }

  @Override
  public void adjust(TypeFdo type) {

    if (!type.getAnnotations().stream()
        .map(AnnotationFdo::getQualifiedName)
        .filter("org.apache.ibatis.annotations.Mapper"::equals)
        .findFirst()
        .isPresent()) {
      return;
    }

    type.getMethods().forEach(this::addCrudPoint);
  }

  void addCrudPoint(MethodFdo method) {
    String signature = StringUtils.substringBefore(method.getQualifiedSignature(), "(");

    String sql = mapperSqls.getProperty(signature);

    if (sql == null) {
      return;
    }

    method.getCrudPoints().addAll(crudExtractor.extract(sql));
  }
}
