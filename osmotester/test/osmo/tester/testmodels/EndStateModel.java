package osmo.tester.testmodels;

import osmo.tester.annotation.Before;
import osmo.tester.annotation.EndState;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Oracle;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.Transition;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * @author Teemu Kanstren
 */
public class EndStateModel {
  @RequirementsField
  private final Requirements req = new Requirements();
  public static final String REQ_HELLO = "hello";
  public static final String REQ_WORLD = "world";
  public static final String REQ_EPIX = "epix";

  public EndStateModel() {
  }

  public Requirements getRequirements() {
    return req;
  }

  @Before
  public void reset() {
    req.clearCoverage();
  }

  @Guard("hello")
  public boolean helloCheck() {
    return !req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @Transition("hello")
  public void transition1() {
    System.out.println("hello");
    req.covered(REQ_HELLO);
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @Transition("world")
  public void epix() {
    System.out.println("world");
    req.covered(REQ_WORLD);
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(REQ_WORLD);
  }

  @Transition("epixx")
  public void epixx() {
    req.covered(REQ_EPIX);
  }

  @Oracle("epixx")
  public void epixxO() {
  }

  @Oracle
  public void stateCheck() {
  }

  @EndState
  public boolean areWeThereYet() {
    return req.getCovered().size() == 3;
  }
}
