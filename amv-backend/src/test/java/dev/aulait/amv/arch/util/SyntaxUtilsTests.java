package dev.aulait.amv.arch.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SyntaxUtilsTests {
  @ParameterizedTest
  @CsvSource({
    "java.lang.String, String",
    "int, int",
    "java.util.List<java.lang.String>, List<String>",
    "'', ''",
    "null, null"
  })
  void testToSimpleType(String qualifiedTypeName, String expected) {
    String actual = SyntaxUtils.toSimpleType(qualifiedTypeName);
    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource({"CodebaseController, Controller", "CallTreeElementVo, Vo", "'', ''", "null, null"})
  void extractStereotypeTest(String typeName, String expected) {
    String actual = SyntaxUtils.extractStereotype(typeName);
    assertEquals(expected, actual);
  }
}
