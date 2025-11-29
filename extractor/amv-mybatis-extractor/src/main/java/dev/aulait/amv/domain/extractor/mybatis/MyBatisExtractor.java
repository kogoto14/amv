package dev.aulait.amv.domain.extractor.mybatis;

import dev.aulait.amv.arch.file.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;

@Slf4j
public class MyBatisExtractor {

  public Properties extract(Path projectDir, String classpath) {

    if (!StringUtils.contains(classpath, "mybatis")) {
      return new Properties();
    }

    Configuration cfg = config(projectDir, classpath);

    // TODO: glob pattern should be configurable
    List<Path> mapperXmls = collect(projectDir.resolve("src/main/resources"), "**/mapper/*.xml");

    mapperXmls.stream().forEach(mapperXml -> parse(mapperXml, cfg));

    Properties props = cfg2props(cfg);

    store(props, projectDir);

    return props;
  }

  Configuration config(Path projectDir, String classpath) {
    Configuration cfg = new Configuration();
    cfg.setNullableOnForEach(true);

    try {
      Resources.setDefaultClassLoader(ClassLoaderFactory.createClassLoader(classpath));

      // TODO: source dir should be configurable
      for (String handledType : extractHandledTypes(projectDir.resolve("src/main/java"))) {
        cfg.getTypeHandlerRegistry().register(handledType, DummyTypeHandler.class.getName());
      }

    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }

    return cfg;
  }

  List<Path> collect(Path sourceDir, String globPattern) {

    log.info(
        "Collecting files from {} with pattern '{}'",
        sourceDir.normalize().toAbsolutePath(),
        globPattern);

    if (!Files.exists(sourceDir)) {
      return List.of();
    }

    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

    try {
      return Files.walk(sourceDir).filter(matcher::matches).toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  void parse(Path mapperXml, Configuration cfg) {
    log.debug("Extracting SQL from {}", mapperXml);

    try (InputStream in = Files.newInputStream(mapperXml)) {
      XMLMapperBuilder mapperParser =
          new XMLMapperBuilder(in, cfg, mapperXml.toString(), cfg.getSqlFragments());
      mapperParser.parse();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  Properties cfg2props(Configuration cfg) {
    Properties props = new Properties();

    for (Object obj : cfg.getMappedStatements()) {

      if (obj instanceof MappedStatement ms) {
        BoundSql bs = ms.getBoundSql(Map.of());
        props.setProperty(ms.getId(), bs.getSql());
      } else {
        // TODO: obj type is sometime Configuration$StrictMap$Ambiguity
        log.info("Unknown mapped statement: {}", obj);
      }
    }

    return props;
  }

  Path store(Properties props, Path outDir) {

    Path outFile = outDir.resolve("mapper.properties");

    log.debug(
        "Storing extracted {} SQLs to {}", props.size(), outFile.normalize().toAbsolutePath());

    try (var out = Files.newOutputStream(outFile)) {
      props.store(out, null);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return outFile;
  }

  List<String> extractHandledTypes(Path projectDir) {
    // TODO: glob pattern should be configurable
    return collect(projectDir, "**/*.java").parallelStream()
        .map(FileUtils::read)
        .map(this::extractHandledTypes)
        .flatMap(List::stream)
        .toList();
  }

  List<String> extractHandledTypes(String javaCode) {

    if (!StringUtils.contains(javaCode, "import org.apache.ibatis.type.MappedTypes;")
        || StringUtils.contains(javaCode, "public class MyBatisExtractor {")) {
      return List.of();
    }

    Map<String, String> importTypes = extractImportTypes(javaCode);

    String mappedTypesContent = StringUtils.substringBetween(javaCode, "@MappedTypes(", ")");

    if (mappedTypesContent == null) {
      return List.of();
    }

    mappedTypesContent = mappedTypesContent.trim().replace("{", "").replace("}", "");

    return Stream.of(mappedTypesContent.split(","))
        .map(String::trim)
        .map(typeExpr -> StringUtils.removeEnd(typeExpr, ".class"))
        .map(typeName -> importTypes.getOrDefault(typeName, typeName))
        .collect(Collectors.toList());
  }

  /**
   * Extract import types from given java code.
   *
   * @param javaCode Java code
   * @return key: simple type name, value: qualified type name
   */
  Map<String, String> extractImportTypes(String javaCode) {
    Pattern importPattern = Pattern.compile("import\\s+([\\w\\.]+);");

    return importPattern
        .matcher(javaCode)
        .results()
        .map(mr -> mr.group(1))
        .collect(Collectors.toMap(imp -> StringUtils.substringAfterLast(imp, "."), imp -> imp));
  }
}
