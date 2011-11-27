package osmo.tester.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.Transition;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * A test model with requirements that can all be covered.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel2 {
  @RequirementsField
  private final Requirements req;
  public static final String REQ_HELLO = "hello";
  public static final String REQ_WORLD = "world";
  public static final String REQ_EPIX = "epix";
  private PrintStream out = NullPrintStream.stream;
  private boolean printFlow = false;

  public ValidTestModel2(Requirements req, PrintStream out) {
    this.req = req;
    this.out = out;
  }

  public ValidTestModel2(Requirements req) {
    this.req = req;
  }

  @BeforeSuite
  public void firstOfAll() {
    if (printFlow) {
      out.print(":beforesuite:");
    }
  }


  @BeforeTest
  public void setup() {
    if (printFlow) {
      out.print(":beforetest:");
    }
  }

  @AfterSuite
  public void lastOfAll() {
    if (printFlow) {
      out.print(":aftersuite:");
    }
  }


  @AfterTest
  public void bob() {
    if (printFlow) {
      out.print(":aftertest:");
    }
  }

  @Guard("hello")
  public boolean helloCheck() {
    return !req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @Transition("hello")
  public void transition1() {
    req.covered(REQ_HELLO);
    out.print(":hello");
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @Transition("world")
  public void epix() {
    req.covered(REQ_WORLD);
    out.print(":world");
  }

  @Guard("epixx")
  public boolean kitted() {
    return req.isCovered(REQ_WORLD);
  }

  @Pre("epixx")
  public void epixxPre() {
    out.print(":epixx_pre");
  }

  @Transition("epixx")
  public void epixx() {
    req.covered(REQ_EPIX);
    out.print(":epixx");
  }

  @Post("epixx")
  public void epixxO() {
    out.print(":epixx_oracle");
  }

  public void setPrintFlow(boolean printFlow) {
    this.printFlow = printFlow;
  }
}
