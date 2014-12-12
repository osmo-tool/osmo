package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.ExplorationEnabler;
import osmo.tester.annotation.GenerationEnabler;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class EmptyTestModel3 {
  private String tags = null;
  private String suite = null;

  @Guard("foo")
  public String hello() {
    return "";
  }

  @TestStep("foo")
  public void epixx() {
  }

  @TestStep("foo")
  public void epixxx() {
  }

  @EndCondition
  public void end() {
  }

  @CoverageValue("my-state")
  public void badArgument(String s1, String s2) {
  }

  @Post("all")
  public void wrong(String p1) {
  }

  @ExplorationEnabler
  public String enableExploration() {
    return null;
  }

  @ExplorationEnabler
  public int enableExploration(int y) {
    return y;
  }

  @GenerationEnabler
  public int enableGeneration() {
    return 1;
  }

  @GenerationEnabler
  public void enableGeneration(int x) {
  }
  
  @BeforeSuite
  public void badBS(String hello) {
  }

  @AfterSuite
  public void badAS(String hello) {
  }

  @BeforeTest
  public void badBT(String hello) {
  }

  @AfterTest
  public void badAT(String hello) {
  }
}
