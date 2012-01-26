package osmo.tester.testmodels;

import osmo.tester.annotation.*;

/** @author Teemu Kanstren */
public class ErrorModel5 {
  @BeforeTest
  public void start1() {
    throw new RuntimeException("@BeforeTest fail");
  }

  @AfterTest
  public void end() {
    throw new RuntimeException("@AfterTest fail");
  }

  @BeforeSuite
  public void beforeAll() {
    throw new RuntimeException("@BeforeSuite fail");
  }

  @AfterSuite
  public void endAll() {
    throw new RuntimeException("@AfterSuite fail");
  }

  @Transition("hello")
  public void transition1() {
    throw new RuntimeException("@Transition fail");
  }

  @Guard("world")
  public boolean listCheck() {
    throw new RuntimeException("@Guard fail");
  }

  @Post
  public void stateOracle() {
    throw new RuntimeException("@Post fail");
  }

  @Pre
  public void beforeEpixx() {
    throw new RuntimeException("@Pre fail");
  }

  @EndCondition
  public boolean end1() {
    throw new RuntimeException("@EndCondition fail");
  }
}
