package osmo.tester.testmodels;

import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.StateName;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/** @author Teemu Kanstren */
public class EmptyTestModel4 {
  @RequirementsField
  private Requirements req = null;
  @TestSuiteField
  private TestSuite suite = null;

  @Guard("foo")
  public boolean hello(String foo) {
    return false;
  }

  @Transition("foo")
  public void epixx() {
  }

  @EndCondition
  public boolean ending(String foo) {
    return false;
  }

  @StateName
  public String noArgument() {
    return "";
  }
}
