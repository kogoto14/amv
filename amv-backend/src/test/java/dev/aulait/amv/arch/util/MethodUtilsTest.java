package dev.aulait.amv.arch.util;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodParamEntity;
import dev.aulait.amv.domain.process.MethodParamEntityId;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MethodUtilsTest {

  @Test
  void testBuildSimpleSignature() {
    MethodEntity method = new MethodEntity();
    method.setName("shortMethod");
    MethodParamEntity p1 = new MethodParamEntity();
    p1.setId(new MethodParamEntityId("dummyType", 1, 1));
    p1.setName("x");
    p1.setType("int");
    MethodParamEntity p2 = new MethodParamEntity();
    p2.setId(new MethodParamEntityId("dummyType", 1, 2));
    p2.setName("str");
    p2.setType("String");
    method.setMethodParams(Set.of(p1, p2));

    String signature = MethodUtils.buildSimpleSignature(method);
    assertEquals("shortMethod(int x, String str)", signature);
  }

  @Test
  void testBuildFormattedSignatureForShort() {
    MethodEntity method = new MethodEntity();
    method.setName("shortMethod");
    MethodParamEntity p1 = new MethodParamEntity();
    p1.setId(new MethodParamEntityId("dummyType", 1, 1));
    p1.setName("x");
    p1.setType("int");
    MethodParamEntity p2 = new MethodParamEntity();
    p2.setId(new MethodParamEntityId("dummyType", 1, 2));
    p2.setName("str");
    p2.setType("String");
    method.setMethodParams(Set.of(p1, p2));

    String signature = MethodUtils.formatSignatureWithLineBreaks(method);
    assertEquals("shortMethod(int x, String str)", signature);
  }

  @Test
  void testBuildFormattedSignatureForLong() {
    MethodEntity method = new MethodEntity();
    method.setName("longMethod");
    MethodParamEntity p1 = new MethodParamEntity();
    p1.setId(new MethodParamEntityId("dummyType", 1, 1));
    p1.setName("x");
    p1.setType("int");
    MethodParamEntity p2 = new MethodParamEntity();
    p2.setId(new MethodParamEntityId("dummyType", 1, 2));
    p2.setName("str");
    p2.setType("String");
    MethodParamEntity p3 = new MethodParamEntity();
    p3.setId(new MethodParamEntityId("dummyType", 1, 3));
    p3.setName("dataMap");
    p3.setType("Map<String, List<Integer>>");
    method.setMethodParams(Set.of(p1, p2, p3));
    String signature = MethodUtils.formatSignatureWithLineBreaks(method);

    String expectedsignature =
        "longMethod(\\n    int x,\\n    String str,\\n    Map<String, List<Integer>> dataMap\\n)";
    assertEquals(expectedsignature, signature);
    System.out.println(signature);
  }
}
