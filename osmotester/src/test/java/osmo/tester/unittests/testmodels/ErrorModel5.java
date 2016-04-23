package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.*;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;

/** @author Teemu Kanstren */
public class ErrorModel5 {
  private TestSuite suite;

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
    throw new AssertionError("@TestStep assert fail");
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
  
  @LastStep
  public void lastStepStanding() {
    
  }

  @EndCondition
  public boolean end1() {
    return false;
  }

  @OnError
  public void error() {
    TestCase test = suite.getCurrentTest();
    test.setAttribute("error", test.getCurrentStep().getName());
  }
}
