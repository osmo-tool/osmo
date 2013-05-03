package osmo.tester.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.StateName;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/** @author Teemu Kanstren */
public class StateDescriptionModel {
  @RequirementsField
  private final Requirements req = new Requirements();
  public static final String TAG_HELLO = "hello";
  public static final String TAG_WORLD = "world";
  public static final String TAG_EPIX = "epix";
  @Variable
  public String firstName = "";
  @Variable
  public String lastName = "";
  @TestSuiteField
  private TestSuite suite = null;
  private String states = "";

  public StateDescriptionModel() {
  }

  public Requirements getRequirements() {
    return req;
  }

  @BeforeTest
  public void reset() {
    req.clearCoverage();
  }

  @Guard("hello")
  public boolean helloCheck() {
    return !req.isCovered(TAG_HELLO) && !req.isCovered(TAG_WORLD) && !req.isCovered(TAG_EPIX);
  }

  @Transition("hello")
  public void transition1() {
    firstName = "hello";
    lastName = "world";
    req.covered(TAG_HELLO);
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(TAG_HELLO) && !req.isCovered(TAG_WORLD) && !req.isCovered(TAG_EPIX);
  }

  @Transition("world")
  public void epix() {
//    System.out.println("world");
    req.covered(TAG_WORLD);
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(TAG_WORLD);
  }

  @Transition("epixx")
  public void epixx() {
    req.covered(TAG_EPIX);
    suite.addValue("global", "bad");
  }

  @Post("epixx")
  public void epixxO() {
  }

  @Post
  public void stateCheck() {
    states += "-"+suite.getCurrentTest().getCurrentStep().getState()+"-";
  }

  @StateName
  public String state(osmo.tester.generator.testsuite.TestStep step) {
    return ""+req.getUniqueCoverage().size();
  }

  public String getStates() {
    return states;
  }
}
