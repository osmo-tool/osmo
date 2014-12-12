package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.testsuite.TestSuite;

/**
 * Test model that has no enabled transition that can be taken.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel1 {
  private TestSuite history = null;

  public TestSuite getHistory() {
    return history;
  }

  @TestStep("world")
  public void epix() {

  }

  @Guard("world")
  public boolean listCheck() {
    return false;
  }

  @TestStep("epixx")
  public void epixx() {

  }

  @Guard("epixx")
  public boolean kitted() {
    return false;
  }
}
