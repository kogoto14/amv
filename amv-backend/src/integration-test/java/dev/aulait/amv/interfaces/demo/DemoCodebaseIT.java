package dev.aulait.amv.interfaces.demo;

import org.junit.jupiter.api.BeforeAll;

public class DemoCodebaseIT {

  @BeforeAll
  static void setUp() {
    DemoScenarioFacade.getInstance().runIfNotLoaded();
  }
}
