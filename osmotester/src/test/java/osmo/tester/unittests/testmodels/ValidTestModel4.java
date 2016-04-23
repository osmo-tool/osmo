package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * A test model with reqs that can all be covered.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel4 {
  private final Requirements req = new Requirements();
  public static final String REQ_HELLO = "hello";
  public static final String REQ_WORLD = "world";
  public static final String REQ_EPIX = "epix";
  private final PrintStream out;
  private TestSuite history = null;

  public ValidTestModel4(PrintStream out) {
    this.out = out;
  }

  @BeforeTest
  public void reset() {
    req.clearCoverage();
  }

  @Guard("hello")
  public boolean helloCheck() {
    return !req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @TestStep("hello")
  public void transition1() {
    req.covered(REQ_HELLO);
    out.print(":hello");
    history.addValue("my_item", "hello");
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @TestStep("world")
  public void epix() {
    req.covered(REQ_WORLD);
    out.print(":world");
    history.addValue("my_item", "world");
    history.addValue("your_item", "foobar");
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(REQ_WORLD);
  }

  @TestStep("epixx")
  public void epixx() {
    req.covered(REQ_EPIX);
    out.print(":epixx");
    history.addValue("my_item", "world");
    history.addValue("your_item", "foobar");
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

  @CoverageValue("my-state")
  public String state1(TestCaseStep step) {
    return ""+req.getUniqueCoverage().size();
  }

  @Pre("all")
  public void savePreState() {
  }

  @Post("all")
  public void savePostState() {
  }
}
