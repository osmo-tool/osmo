package osmo.tester.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.ExplorationEnabler;
import osmo.tester.annotation.GenerationEnabler;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.util.Map;

/** @author Teemu Kanstren */
public class EmptyTestModel1 {
  private Requirements req = new Requirements();
  private TestSuite history = null;

  public TestSuite getHistory() {
    return history;
  }

  @BeforeTest
  public void start1() {

  }

  @BeforeTest
  public void start2() {

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

  @Guard("!world")
  public boolean negato() {
    return true;
  }

  @Transition("hello")
  public void transition1() {

  }

  @Transition("world")
  public void epix() {

  }

  @Guard
  public boolean gWorld() {
    return false;
  }

  @Guard
  public boolean g2World() {
    return false;
  }

  @Transition("epixx")
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

  @Pre({"hello", "epixx"})
  public void commonPre() {
  }

  @Post({"hello", "epixx"})
  public void commonOracle() {
  }

  @EndCondition
  public boolean end1() {
    return false;
  }

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
