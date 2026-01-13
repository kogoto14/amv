package dev.aulait.amv.domain.extractor.java.FlowStatementTestResources;

import java.io.IOException;

public class TryCatchFinallyFlowStatements {

  public void trycatchStatement() {
    try {
      System.out.println("try");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void tryCatchNestedStatement() {
    try {
      System.out.println("try");

      try {
        System.out.println("tryNest");

        if (System.currentTimeMillis() < 0) {
          throw new IllegalArgumentException("boom");
        }

        if (System.nanoTime() < 0) {
          throw new IOException("io");
        }
      } catch (IllegalArgumentException e) {
        throw e;
      } catch (IOException e) {
        throw e;
      }
    } catch (IOException | IllegalArgumentException e) {
      System.out.println("outer ex");
    }
  }
}
