package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/** @author Teemu Kanstren */
public class EmptyTestModel2 {
  private Requirements req1 = new Requirements();
  private Requirements req2 = new Requirements();
  private TestSuite suite1 = null;
  private TestSuite suite2 = null;

  @Guard("foo")
  public boolean hello() {
    return false;
  }
}
