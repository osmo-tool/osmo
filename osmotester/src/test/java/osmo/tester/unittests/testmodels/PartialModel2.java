package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.ExplorationEnabler;
import osmo.tester.annotation.Group;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/** @author Teemu Kanstren */
@Group("part2-group")
public class PartialModel2 {
  private final Requirements req;
  private TestSuite history;
  public static final String TAG_HELLO = "hello";
  public static final String TAG_WORLD = "world";
  public static final String TAG_EPIX = "epix";
  private PrintStream out;

  public PartialModel2(Requirements req) {
    this(req, null);
  }

  public PartialModel2(Requirements req, PrintStream out) {
    this.req = req;
    this.out = out;
  }

  @BeforeSuite
  public void beforeAll() {
    if (out == null) {
      out = System.out;
    }
  }

  @AfterSuite
  public void endAll() {
  }

  @BeforeTest
  public void start2() {
  }

  @AfterTest
  public void end2() {
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

  @TestStep("epixx")
  public void epixx() {
    req.covered(TAG_EPIX);
    out.print(":epixx");
  }

  @Post({"hello", "world"})
  public void sharedCheck() {
    out.print(":two_oracle");
  }

  @EndCondition
  public boolean ec2() {
    return false;
  }

  @ExplorationEnabler
  public void enabler1() {
  }

  @CoverageValue
  public String state2(TestCaseStep step) {
    return step.getName()+"-hello2";
  }
}
