package osmo.tester.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.dataflow.CollectionCount;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

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
  private Collection<String> values = new ArrayList<String>();
  @Variable
  private CollectionCount valueCount = new CollectionCount(values);
  private final PrintStream out;

  public VariableModel2() {
    out = NullPrintStream.stream;
  }

  public VariableModel2(PrintStream out) {
    this.out = out;
  }

  @Guard("first")
  public boolean allowFirst() {
    return !first;
  }

  @Transition("first")
  public void first() {
    first = true;
    out.println(":first:");
  }

  @Guard("second")
  public boolean allowSecond() {
    return first && !second;
  }

  @Transition("second")
  public void second() {
    second = true;
    out.println(":second:");
  }

  @Guard("third")
  public boolean allowThird() {
    return first && second;
  }

  @Transition("third")
  public void third() {
    range.next();
    String next = set.next();
    values.add(next);
    out.println(":third=" + next + ":");
  }

  public TestSuite getSuite() {
    return suite;
  }
}
