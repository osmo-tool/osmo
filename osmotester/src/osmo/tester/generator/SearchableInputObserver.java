package osmo.tester.generator;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.dataflow.InputObserver;

/** @author Teemu Kanstren */
public class SearchableInputObserver implements InputObserver {
  private final TestSuite suite;

  public SearchableInputObserver(TestSuite suite) {
    this.suite = suite;
  }

  @Override
  public void value(String variable, Object value) {
    suite.getCurrentTest().addVariableValue(variable, value);
  }
}
