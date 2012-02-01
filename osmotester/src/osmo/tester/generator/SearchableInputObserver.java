package osmo.tester.generator;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.dataflow.InputObserver;

/**
 * Observer that is attached to all {@link osmo.tester.model.dataflow.SearchableInput} instances to
 * track generated data in the generated test suite.
 *
 * @author Teemu Kanstren
 */
public class SearchableInputObserver implements InputObserver {
  /** The suite where the observed values are stored. */
  private final TestSuite suite;

  public SearchableInputObserver(TestSuite suite) {
    this.suite = suite;
  }

  @Override
  public void value(String variable, Object value) {
    suite.getCurrentTest().addVariableValue(variable, value);
  }
}
