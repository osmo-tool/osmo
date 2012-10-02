package osmo.tester.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.Requirements;

import java.io.PrintStream;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * A test model with requirements that can all be covered.
 *
 * @author Teemu Kanstren
 */
public class TestStepModel {
  @RequirementsField
  private final Requirements req = new Requirements();
  public static final String REQ_HELLO = "hello";
  public static final String REQ_WORLD = "world";
  public static final String REQ_EPIX = "epix";
  private final PrintStream out;
  private int lastStep1TestCount = 0;
  private int lastStep2TestCount = 0;
  private int lastStep1SuiteCount = 0;
  private int lastStep2SuiteCount = 0;

  public TestStepModel(PrintStream out) {
    this.out = out;
  }

  @BeforeTest
  public void reset() {
    req.clearCoverage();
    lastStep1TestCount = 0;
    lastStep2TestCount = 0;
  }

  @AfterTest
  @BeforeSuite
  @AfterSuite
  public void empty() {

  }

  @Guard("hello")
  public boolean helloCheck() {
    return !req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @TestStep("hello")
  public void transition1() {
    req.covered(REQ_HELLO);
    out.print(":hello");
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @TestStep("world")
  public void epix() {
    req.covered(REQ_WORLD);
    out.print(":world");
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(REQ_WORLD);
  }

  @Pre("epixx")
  public void epixxPre(Map<String, Object> p) {
    out.print(":epixx_pre");
    assertEquals("First pre-method should have no parameters.", 0, p.size());
    p.put("test", "hello post");
  }

  @TestStep("epixx")
  public void epixx() {
    req.covered(REQ_EPIX);
    out.print(":epixx");
  }

  @Post("epixx")
  public void epixxO(Map<String, Object> p) {
    out.print(":epixx_oracle");
    assertEquals("Post should maintain pre-parameters.", 1, p.size());
    assertEquals("Content of parameter should remain from pre- to post.", "hello post", p.get("test"));
  }

  @Post
  public void stateCheck() {
    out.print(":gen_oracle");
  }
  
  @LastStep
  public void finish() {
    out.print(":last_step");
    lastStep1TestCount++;
    lastStep1SuiteCount++;
  }

  @LastStep
  public void powerball() {
    out.print(":last_step");
    lastStep2TestCount++;
    lastStep2SuiteCount++;
  }

  public int getLastStep1TestCount() {
    return lastStep1TestCount;
  }

  public int getLastStep2TestCount() {
    return lastStep2TestCount;
  }

  public int getLastStep1SuiteCount() {
    return lastStep1SuiteCount;
  }

  public int getLastStep2SuiteCount() {
    return lastStep2SuiteCount;
  }
}
