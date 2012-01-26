package osmo.tester.testmodels;

import osmo.tester.annotation.*;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.util.Map;

/** @author Teemu Kanstren */
public class ErrorModel1 {
  @BeforeTest
  public void start1() {
    throw new RuntimeException("@BeforeTest fail");
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
