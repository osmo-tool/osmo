package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
public class ErrorModel2 {
  @BeforeTest
  public void start1() {
  }

  @AfterTest
  public void end() {
    throw new RuntimeException("@AfterTest fail");
  }

  @BeforeSuite
  public void beforeAll() {
  }

  @AfterSuite
  public void endAll() {
  }

  @Transition("hello")
  public void transition1() {
  }

  @Guard("hello")
  public boolean listCheck() {
    return true;
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
