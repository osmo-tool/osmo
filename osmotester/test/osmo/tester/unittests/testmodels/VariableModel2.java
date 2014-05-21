package osmo.tester.unittests.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.CollectionCount;
import osmo.tester.model.data.ToStringValue;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;
import osmo.tester.unittests.generation.TestVariable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class VariableModel2 {
  @Variable
  private boolean first = false;
  @Variable
  private boolean second = false;
  private TestSuite suite = null;
  @Variable
  private ValueRange<Integer> range = new ValueRange<>(1, 5);
  @Variable("named-set")
  private ValueSet<String> set = new ValueSet<>("v1", "v2", "v3");
  private Collection<String> values = new ArrayList<>();
  @Variable
  private CollectionCount valueCount = new CollectionCount(values);
  private TestVariable testVariable = new TestVariable("MyTV");
  @Variable("Hello There")
  private ToStringValue toStringVariable = new ToStringValue(testVariable);
  private final PrintStream out;

  public VariableModel2() {
    out = NullPrintStream.stream;
  }

  public VariableModel2(PrintStream out) {
    this.out = out;
  }

  @BeforeTest
  public void reset() {
    first = false;
    second = false;
    values.clear();
    out.println("NEW TEST");
  }

  @Guard("first")
  public boolean allowFirst() {
    return !first;
  }

  @TestStep("first")
  public void first() {
    first = true;
    out.println(":first:");
  }

  @Guard("second")
  public boolean allowSecond() {
    return first && !second;
  }

  @TestStep("second")
  public void second() {
    second = true;
    out.println(":second:");
  }

  @Guard("third")
  public boolean allowThird() {
    return first && second;
  }

  @TestStep("third")
  public void third() {
    out.println("range:" + range.random());
    String next = set.random();
    values.add(next);
    out.println(":third=" + next + ":");
  }

  public TestSuite getSuite() {
    return suite;
  }
}
