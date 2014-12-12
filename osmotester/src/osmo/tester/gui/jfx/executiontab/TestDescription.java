package osmo.tester.gui.jfx.executiontab;

import osmo.tester.generator.testsuite.TestCase;

/**
 * @author Teemu Kanstren
 */
public class TestDescription {
  private final TestCase test;

  public TestDescription(TestCase test) {
    this.test = test;
  }

  @Override
  public String toString() {
    return test.getName();
  }

  public TestCase getTest() {
    return test;
  }
}
