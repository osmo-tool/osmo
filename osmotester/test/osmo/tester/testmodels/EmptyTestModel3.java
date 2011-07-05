package osmo.tester.testmodels;

import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Oracle;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;

/**
 * @author Teemu Kanstren
 */
public class EmptyTestModel3 {
  @RequirementsField
  private String requirements = null;
  @TestSuiteField
  private String suite = null;

  @Guard("foo")
  public String hello() {
    return "";
  }

  @Transition("foo")
  public void epixx() {
  }

  @EndCondition
  public void end() {
  }

  @Oracle
  public void wrong(String p1) {
  }
}
