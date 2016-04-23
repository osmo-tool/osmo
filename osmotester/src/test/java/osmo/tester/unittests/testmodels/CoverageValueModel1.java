package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/** @author Teemu Kanstren */
public class CoverageValueModel1 {
  private final Requirements req = new Requirements();
  public static final String TAG_HELLO = "hello";
  public static final String TAG_WORLD = "world";
  public static final String TAG_EPIX = "epix";
  @Variable
  public String firstName = "";
  @Variable
  public String lastName = "";
  private TestSuite suite = null;

  public CoverageValueModel1() {
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

  @TestStep("hello")
  public void transition1() {
    firstName = "hello";
    lastName = "world";
    req.covered(TAG_HELLO);
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(TAG_HELLO) && !req.isCovered(TAG_WORLD) && !req.isCovered(TAG_EPIX);
  }

  @TestStep("world")
  public void epix() {
//    System.out.println("world");
    req.covered(TAG_WORLD);
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(TAG_WORLD);
  }

  @TestStep("epixx")
  public void epixx() {
    req.covered(TAG_EPIX);
    suite.addValue("global", "bad");
  }

  @Post("epixx")
  public void epixxO() {
  }

  @Post("all")
  public void stateCheck() {
  }

  @CoverageValue("my-state")
  public String state(TestCaseStep step) {
    return ""+req.getUniqueCoverage().size();
  }
}
