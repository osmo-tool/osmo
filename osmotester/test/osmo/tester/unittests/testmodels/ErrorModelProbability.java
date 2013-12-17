package osmo.tester.unittests.testmodels;

import osmo.common.Randomizer;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.data.ValueRange;

/** @author Teemu Kanstren */
public class ErrorModelProbability {
  private Randomizer rand = new Randomizer();
  
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
    //throw exception with 10% probability to allow for search
    if (rand.nextInt(1, 10) == 1) throw new RuntimeException("@EndCondition fail");
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
