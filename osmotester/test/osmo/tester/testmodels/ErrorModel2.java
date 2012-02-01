package osmo.tester.testmodels;

import osmo.tester.annotation.*;

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

  @Post
  public void stateOracle() {
  }

  @Pre
  public void beforeEpixx() {
  }

  @EndCondition
  public boolean end1() {
    return false;
  }
}
