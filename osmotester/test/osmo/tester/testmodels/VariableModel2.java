package osmo.tester.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/** @author Teemu Kanstren */
public class VariableModel2 {
  @Variable
  private boolean first = false;
  @Variable
  private boolean second = false;
  @TestSuiteField
  private TestSuite suite = new TestSuite();
  private ValueRange<Integer> range = new ValueRange<Integer>(1, 5);
  private ValueSet<String> set = new ValueSet<String>("v1", "v2", "v3");

  public VariableModel2() {
  }

  @Guard("first")
  public boolean allowFirst() {
    return !first;
  }

  @Transition("first")
  public void first() {
    first = true;
  }

  @Guard("second")
  public boolean allowSecond() {
    return first && !second;
  }

  @Transition("second")
  public void second() {
    second = true;
  }

  @Guard("third")
  public boolean allowThird() {
    return first && second;
  }

  @Transition("third")
  public void third() {
    range.next();
    set.next();
  }

  public TestSuite getSuite() {
    return suite;
  }
}
