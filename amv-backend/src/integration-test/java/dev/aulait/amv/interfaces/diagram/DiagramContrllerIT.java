package dev.aulait.amv.interfaces.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.interfaces.demo.DemoCodebaseIT;
import dev.aulait.amv.interfaces.process.MethodDto;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DiagramContrllerIT extends DemoCodebaseIT {

  DiagramClient client = new DiagramClient();

  @Test
  public void testCallTree() {

    List<CallTreeDto> result = client.callTree("CodebaseController.save");

    CallTreeDto firstCallTree = result.getFirst();
    assertEquals("CodebaseController", firstCallTree.getMethod().getType());
    assertEquals("save", firstCallTree.getMethod().getName());

    MethodDto secondMethod = firstCallTree.getCallTree().get(1).getMethod();
    assertEquals("BeanUtils", secondMethod.getType());
    assertEquals("map(Object src, Class<T> dstType)", secondMethod.getSimpleSignature());
    assertEquals(
        "dev.aulait.amv.arch.util.BeanUtils.map(java.lang.Object, java.lang.Class<T>)",
        secondMethod.getQualifiedSignature());

    MethodDto lastMethod = firstCallTree.getCallTree().getLast().getMethod();
    assertEquals("CodebaseEntity", lastMethod.getType());
    assertEquals("getId()", lastMethod.getSimpleSignature());
    assertEquals(
        "dev.aulait.amv.domain.project.CodebaseEntity.getId()", lastMethod.getQualifiedSignature());
  }
}
