package osmo.tester.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * A test model with tags that can all be covered.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel4 {
  @RequirementsField
  private final Requirements req = new Requirements();
  public static final String TAG_HELLO = "hello";
  public static final String TAG_WORLD = "world";
  public static final String TAG_EPIX = "epix";
  private final PrintStream out;
  @TestSuiteField
  private TestSuite history = null;
  private String states = "";

  public ValidTestModel4(PrintStream out) {
    this.out = out;
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
    req.covered(TAG_HELLO);
    out.print(":hello");
    history.addValue("my_item", "hello");
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(TAG_HELLO) && !req.isCovered(TAG_WORLD) && !req.isCovered(TAG_EPIX);
  }

  @Transition("world")
  public void epix() {
    req.covered(TAG_WORLD);
    out.print(":world");
    history.addValue("my_item", "world");
    history.addValue("your_item", "foobar");
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(TAG_WORLD);
  }

  @Transition("epixx")
  public void epixx() {
    req.covered(TAG_EPIX);
    out.print(":epixx");
    history.addValue("my_item", "world");
    history.addValue("your_item", "foobar");
  }

  @Post("epixx")
  public void epixxO() {
    out.print(":epixx_oracle");
  }

  @Post
  public void stateCheck() {
    out.print(":gen_oracle");
  }

  @Post({"hello", "world"})
  public void sharedCheck() {
    out.print(":two_oracle");
  }

  @CoverageValue("my-state")
  public String state1(TestCaseStep step) {
    return ""+req.getUniqueCoverage().size();
  }

  @Pre
  public void savePreState() {
    states += ":"+history.getCurrentTest().getCurrentStep().getStatesFor("my-state")+":";
  }

  @Post
  public void savePostState() {
    states += "-"+history.getCurrentTest().getCurrentStep().getStatesFor("my-state")+"-";
  }

  public String getStates() {
    return states;
  }
}
