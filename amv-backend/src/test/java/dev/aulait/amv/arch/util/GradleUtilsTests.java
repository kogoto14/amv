package dev.aulait.amv.arch.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

class GradleUtilsTests {

  Path projectDir = Path.of("target/spring-boot-realworld-example-app");

  boolean exists() {
    return Files.exists(projectDir);
  }

  @Test
  // TODO: Fix this test to create the test projectDir before running the test
  @EnabledIf("exists")
  void generateClasspathTest() {
    String classpath = GradleUtils.generateClasspath(projectDir);

    assertTrue(StringUtils.isNotEmpty(classpath));
  }
}
