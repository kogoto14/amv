package dev.aulait.amv.domain.extractor.java;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.amv.arch.test.ResourceUtils;
import dev.aulait.amv.domain.extractor.fdo.FlowStatementFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFileDataFactory;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class FlowStatementTests {

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void ifRelatedFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "IfRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).get();

    MethodFdo method = SourceFileDataFactory.findMethodByName(extractedSource, "ifFlowStatement");

    List<FlowStatementFdo> flowStatements =
        method.getMethodCalls().stream()
            .flatMap(call -> java.util.stream.Stream.ofNullable(call.getFlowStatement()))
            .toList();

    FlowStatementFdo ifFs =
        flowStatements.stream()
            .filter(fs -> FlowStatementKind.IF.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();

    assertEquals(FlowStatementKind.IF.code(), ifFs.getKind());
    assertEquals("flg", ifFs.getContent());
    assertEquals(11, ifFs.getLineNo());

    FlowStatementFdo elseIfFs =
        flowStatements.stream()
            .filter(fs -> FlowStatementKind.ELSE_IF.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();

    assertEquals(FlowStatementKind.ELSE_IF.code(), elseIfFs.getKind());
    assertEquals("flg", elseIfFs.getContent());
    assertEquals(13, elseIfFs.getLineNo());

    FlowStatementFdo elseFs =
        flowStatements.stream()
            .filter(fs -> FlowStatementKind.ELSE.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();

    assertEquals(FlowStatementKind.ELSE.code(), elseFs.getKind());
    assertEquals("else", elseFs.getContent());
    assertEquals(15, elseFs.getLineNo());
  }

  @Test
  void multipleSyntaxFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "IfRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).get();

    MethodFdo method =
        SourceFileDataFactory.findMethodByName(extractedSource, "duplicateCaseFlowStatement");

    List<FlowStatementFdo> flowStatements = method.getFlowStatements();

    FlowStatementFdo ifFs =
        flowStatements.stream()
            .filter(fs -> FlowStatementKind.IF.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();

    assertEquals(FlowStatementKind.IF.code(), ifFs.getKind());
    assertEquals("readTestList.isEmpty()", ifFs.getContent());
    assertEquals(22, ifFs.getLineNo());

    FlowStatementFdo forFs =
        flowStatements.stream()
            .filter(fs -> FlowStatementKind.FOR.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();

    assertEquals(FlowStatementKind.FOR.code(), forFs.getKind());
    assertEquals("String testVal : readTestList", forFs.getContent());
    assertEquals(28, forFs.getLineNo());
  }

  @Test
  void ifNestedFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "IfRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).get();

    MethodFdo method =
        SourceFileDataFactory.findMethodByName(extractedSource, "ifNestedFlowStatement");

    List<FlowStatementFdo> ifFs =
        method.getFlowStatements().stream()
            .filter(fs -> FlowStatementKind.IF.code().equals(fs.getKind()))
            .toList();

    for (FlowStatementFdo fs : ifFs) {
      if (fs.getLineNo() == 35) {
        assertEquals(FlowStatementKind.IF.code(), fs.getKind());
        assertEquals("flg", fs.getContent());
        assertEquals(35, fs.getLineNo());
      } else if (fs.getLineNo() == 37) {
        assertEquals(FlowStatementKind.IF.code(), fs.getKind());
        assertEquals("flg", fs.getContent());
        assertEquals(37, fs.getLineNo());
      } else if (fs.getLineNo() == 38) {
        assertEquals(FlowStatementKind.IF.code(), fs.getKind());
        assertEquals("flg", fs.getContent());
        assertEquals(38, fs.getLineNo());
      }
    }

    FlowStatementFdo elseIfFs =
        method.getFlowStatements().stream()
            .filter(fs -> FlowStatementKind.ELSE_IF.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();
    assertEquals(FlowStatementKind.ELSE_IF.code(), elseIfFs.getKind());
    assertEquals("flg", elseIfFs.getContent());
    assertEquals(45, elseIfFs.getLineNo());

    List<FlowStatementFdo> elseFs =
        method.getFlowStatements().stream()
            .filter(fs -> FlowStatementKind.ELSE.code().equals(fs.getKind()))
            .toList();
    for (FlowStatementFdo fs : elseFs) {
      if (fs.getLineNo() == 42) {
        assertEquals(FlowStatementKind.ELSE.code(), fs.getKind());
        assertEquals("else", fs.getContent());
        assertEquals(42, fs.getLineNo());
      } else if (fs.getLineNo() == 47) {
        assertEquals(FlowStatementKind.ELSE.code(), fs.getKind());
        assertEquals("else", fs.getContent());
        assertEquals(47, fs.getLineNo());
      }
    }
  }

  @Test
  void forRelatedFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "ForRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    MethodFdo method =
        SourceFileDataFactory.findMethodByName(extractedSource, "forRelatedStatement");

    List<FlowStatementFdo> flowStatements = method.getFlowStatements();

    for (FlowStatementFdo fs : flowStatements) {

      if ("i <= 10".equals(fs.getContent())) {
        assertEquals(FlowStatementKind.FOR.code(), fs.getKind());
        assertEquals("i <= 10", fs.getContent());
        assertEquals(10, fs.getLineNo());
      } else if ("Integer num : list".equals(fs.getContent())) {
        assertEquals(FlowStatementKind.FOR.code(), fs.getKind());
        assertEquals("Integer num : list", fs.getContent());
        assertEquals(14, fs.getLineNo());
      }
    }
  }

  @Test
  void forNestedFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "ForRelatedFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    MethodFdo method =
        SourceFileDataFactory.findMethodByName(extractedSource, "forNestedFlowStatement");

    List<FlowStatementFdo> flowStatements = method.getFlowStatements();

    for (FlowStatementFdo fs : flowStatements) {

      if ("i <= 5".equals(fs.getContent())) {
        assertEquals(FlowStatementKind.FOR.code(), fs.getKind());
        assertEquals("i <= 5", fs.getContent());
        assertEquals(21, fs.getLineNo());
      } else if ("Integer num : list".equals(fs.getContent())) {
        assertEquals(FlowStatementKind.FOR.code(), fs.getKind());
        assertEquals("Integer num : list", fs.getContent());
        assertEquals(22, fs.getLineNo());
      }
    }
  }

  @Test
  void returnFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "ReturnFlowStatement.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    MethodFdo method = SourceFileDataFactory.findMethodByName(extractedSource, "returnStatement");

    FlowStatementFdo flowStatement = method.getMethodCalls().get(0).getFlowStatement();

    assertEquals(FlowStatementKind.RETURN.code(), flowStatement.getKind());
    assertEquals("innerRtnMethod()", flowStatement.getContent());
    assertEquals(6, flowStatement.getLineNo());
  }

  @Test
  void returnNestedFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "ReturnFlowStatement.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    MethodFdo method =
        SourceFileDataFactory.findMethodByName(extractedSource, "returnNestedStatement");

    List<FlowStatementFdo> flowStatements = method.getFlowStatements();

    List<FlowStatementFdo> returnFs =
        flowStatements.stream()
            .filter(fs -> FlowStatementKind.RETURN.code().equals(fs.getKind()))
            .toList();

    for (FlowStatementFdo fs : returnFs) {
      if ("innerRtnMethod()".equals(fs.getContent())) {
        assertEquals(FlowStatementKind.RETURN.code(), fs.getKind());
        assertEquals("innerRtnMethod()", fs.getContent());
        assertEquals(12, fs.getLineNo());
      } else if ("defaultRtnMethod()".equals(fs.getContent())) {
        assertEquals(FlowStatementKind.RETURN.code(), fs.getKind());
        assertEquals("defaultRtnMethod()", fs.getContent());
        assertEquals(14, fs.getLineNo());
      }
    }
  }

  @Test
  void tryCatchFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "TryCatchFinallyFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    MethodFdo method = SourceFileDataFactory.findMethodByName(extractedSource, "trycatchStatement");

    FlowStatementFdo tryFs =
        method.getFlowStatements().stream()
            .filter(fs -> FlowStatementKind.TRY.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();

    assertEquals("try", tryFs.getContent());
    assertEquals(8, tryFs.getLineNo());

    FlowStatementFdo catchFs =
        method.getFlowStatements().stream()
            .filter(fs -> FlowStatementKind.CATCH.code().equals(fs.getKind()))
            .findFirst()
            .orElseThrow();

    assertEquals("catch (Exception e)", catchFs.getContent());
    assertEquals(10, catchFs.getLineNo());
  }

  @Test
  void tryCatchNestedFlowStatementKindCodeTest() {
    Path sourceFile = ResourceUtils.res2path(this, "TryCatchFinallyFlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).orElseThrow();

    MethodFdo method =
        SourceFileDataFactory.findMethodByName(extractedSource, "tryCatchNestedStatement");

    List<FlowStatementFdo> tryFs =
        method.getFlowStatements().stream()
            .filter(fs -> FlowStatementKind.TRY.code().equals(fs.getKind()))
            .toList();

    for (FlowStatementFdo fs : tryFs) {
      if (fs.getLineNo() == 16) {
        assertEquals(FlowStatementKind.TRY.code(), fs.getKind());
        assertEquals("try", fs.getContent());
        assertEquals(16, fs.getLineNo());
      } else if (fs.getLineNo() == 19) {
        assertEquals(FlowStatementKind.TRY.code(), fs.getKind());
        assertEquals("try", fs.getContent());
        assertEquals(19, fs.getLineNo());
      }
    }

    List<FlowStatementFdo> catchFs =
        method.getFlowStatements().stream()
            .filter(fs -> FlowStatementKind.CATCH.code().equals(fs.getKind()))
            .toList();
    for (FlowStatementFdo fs : catchFs) {
      if (fs.getLineNo() == 29) {
        assertEquals(FlowStatementKind.CATCH.code(), fs.getKind());
        assertEquals("catch (IllegalArgumentException e)", fs.getContent());
        assertEquals(29, fs.getLineNo());
      } else if (fs.getLineNo() == 31) {
        assertEquals(FlowStatementKind.CATCH.code(), fs.getKind());
        assertEquals("catch (IOException e)", fs.getContent());
        assertEquals(31, fs.getLineNo());
      } else if (fs.getLineNo() == 34) {
        assertEquals(FlowStatementKind.CATCH.code(), fs.getKind());
        assertEquals("catch (IOException | IllegalArgumentException e)", fs.getContent());
        assertEquals(34, fs.getLineNo());
      }
    }
  }
}
