package osmo.tester.testmodels;

import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.ExplorationEnabler;
import osmo.tester.annotation.GenerationEnabler;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.StateName;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
public class EmptyTestModel3 {
  @RequirementsField
  private String tags = null;
  @TestSuiteField
  private String suite = null;

  @Guard("foo")
  public String hello() {
    return "";
  }

  @Transition("foo")
  public void epixx() {
  }

  @EndCondition
  public void end() {
  }

  @StateName
  public void badArgument(String s1, String s2) {
  }

  @Post
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
}
