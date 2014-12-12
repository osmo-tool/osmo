package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * A test model with tags that can all be covered.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel5 {
  private final Requirements req = new Requirements();
  public static final String TAG_HELLO = "hello";
  public static final String TAG_WORLD = "world";
  public static final String TAG_EPIX = "epix";
  private final PrintStream out;

  public ValidTestModel5(PrintStream out) {
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

  @TestStep("hello")
  public void transition1() {
    req.covered(TAG_HELLO);
    out.print(":hello");
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(TAG_HELLO) && !req.isCovered(TAG_WORLD) && !req.isCovered(TAG_EPIX);
  }

  @TestStep("world")
  public void epix() {
    req.covered(TAG_WORLD);
    out.print(":world");
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(TAG_WORLD);
  }

  @TestStep("epixx")
  public void epixx() {
    req.covered(TAG_EPIX);
    out.print(":epixx");
  }

  @Post("epixx")
  public void epixxO() {
    out.print(":epixx_oracle");
  }

  @Post("all")
  public void stateCheck() {
    out.print(":gen_oracle");
  }

  @Post({"hello", "world"})
  public void sharedCheck() {
    out.print(":two_oracle");
  }
  
  @LastStep
  public void last() {
    out.print(":last");
  }

  @EndCondition
  public boolean end() {
    return req.getUniqueCoverage().size() >= 2;
  }
  
}
