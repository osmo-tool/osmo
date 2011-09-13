package osmo.tester.testmodels;

import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.reporting.tests.CSV;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class ParameterModel1 {
  @TestSuiteField
  private TestSuite suite = new TestSuite();
  private CSV csv;
  private int i1 = 1;
  private int i2 = 2;
  private int i3 = 3;

  public ParameterModel1() {
    csv = new CSV(suite);
  }

  @Transition("three-params")
  public void threeParams() {
    System.out.println("three");
    csv.addParameter("i1", i1++);
    csv.addParameter("i2", i2++);
    csv.addParameter("i3", i3++);
  }

  @Transition("two-params")
  public void twoParams() {
    System.out.println("two");
    csv.addParameter("i1", i1++);
    csv.addParameter("i2", i2++);
  }

  @Transition("empty")
  public void empty() {
    System.out.println("empty");
  }

  public List<String> getResult() {
    return csv.report();
  }
}
