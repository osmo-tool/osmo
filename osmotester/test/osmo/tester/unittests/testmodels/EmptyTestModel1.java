package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.*;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/** @author Teemu Kanstren */
public class EmptyTestModel1 {
  private Requirements req = new Requirements();
  private TestSuite history = null;

  public TestSuite getHistory() {
    return history;
  }

  @Description("Start here")
  @BeforeTest
  public void start1() {

  }

  @Description("Before test we do this")
  @BeforeTest
  public void start2() {

  }

  @Description("After test we do this")
  @AfterTest
  public void end() {

  }

  @Description("Before the suite looks like this")
  @BeforeSuite
  public void beforeAll() {

  }

  @Description("After the suite looks like this")
  @AfterSuite
  public void endAll() {

  }

  @Description("Negative guard looks like this")
  @Guard("!world")
  public boolean negato() {
    return true;
  }

  @Description("Greetings are provided here")
  @TestStep("hello")
  public void transition1() {

  }

  @Description("World is round")
  @TestStep("world")
  public void epix() {

  }

  @Description("World is guarded here")
  @Guard
  public boolean gWorld() {
    return false;
  }

  @Guard
  public boolean g2World() {
    return false;
  }

  @TestStep("epixx")
  public void epixx() {

  }

  @Guard("epixx")
  public boolean kitted() {
    return false;
  }

  @Guard({"epixx", "world"})
  public boolean gaagaa() {
    return false;
  }

  @Post("all")
  public void stateOracle() {
  }

  @Pre("epixx")
  public void beforeEpixx() {
  }

  @Post("epixx")
  public void epixxOracle() {
  }

  @Description("Pre one")
  @Pre({"hello", "epixx"})
  public void commonPre() {
  }

  @Description("Post one")
  @Post({"hello", "epixx"})
  public void commonOracle() {
  }

  @Description("Extra end condition one")
  @EndCondition
  public boolean end1() {
    return false;
  }

  @Description("Extra end condition two")
  @EndCondition
  public boolean end2() {
    return false;
  }

  @ExplorationEnabler
  public void enableExploration() {
  }

  @GenerationEnabler
  public void enableGeneration() {
  }
}
