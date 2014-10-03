package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class ErrorModel6 {
  @BeforeTest
  public void start1() {
  }

  @AfterTest
  public void end() {
  }

  @BeforeSuite
  public void beforeAll() {
  }

  @AfterSuite
  public void endAll() {
  }

  @TestStep("hello")
  public void transition1() {
  }

  @Guard("hello")
  public boolean listCheck() {
    throw new RuntimeException("@Guard fail");
  }

  @Post("all")
  public void stateOracle() {
  }

  @Pre("all")
  public void beforeEpixx() {
  }

  @EndCondition
  public boolean end1() {
    return false;
  }
}
