package osmo.tester.unittests.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.model.TestModels;

import java.io.PrintStream;

/**
 * A test model with reqs that can all be covered.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel2 {
  private final Requirements reqs;
  public static final String REQ_HELLO = "hello";
  public static final String REQ_WORLD = "world";
  public static final String REQ_EPIX = "epix";
  private PrintStream out = NullPrintStream.stream;
  private boolean printFlow = false;

  public ValidTestModel2(Requirements reqs, PrintStream out) {
    this.reqs = reqs;
    this.out = out;
  }

  public ValidTestModel2(Requirements reqs) {
    this.reqs = reqs;
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
    return !reqs.isCovered(REQ_HELLO) && !reqs.isCovered(REQ_WORLD) && !reqs.isCovered(REQ_EPIX);
  }

  @TestStep("hello")
  public void transition1() {
    reqs.covered(REQ_HELLO);
    out.print(":hello");
  }

  @Guard("world")
  public boolean worldCheck() {
    return reqs.isCovered(REQ_HELLO) && !reqs.isCovered(REQ_WORLD) && !reqs.isCovered(REQ_EPIX);
  }

  @TestStep("world")
  public void epix() {
    reqs.covered(REQ_WORLD);
    out.print(":world");
  }

  @Guard("epixx")
  public boolean kitted() {
    return reqs.isCovered(REQ_WORLD);
  }

  @Pre("epixx")
  public void epixxPre() {
    out.print(":epixx_pre");
  }

  @TestStep("epixx")
  public void epixx() {
    reqs.covered(REQ_EPIX);
    out.print(":epixx");
  }

  @Post("epixx")
  public void epixxO() {
    out.print(":epixx_oracle");
  }
  
  @LastStep
  public void last() {
    out.print(":last");
  }

  public void setPrintFlow(boolean printFlow) {
    this.printFlow = printFlow;
  }
  
  public static class MyModelFactory implements ModelFactory {
    private Requirements reqs = null;
    private PrintStream ps = null;

    public MyModelFactory(Requirements reqs, PrintStream ps) {
      this.reqs = reqs;
      this.ps = ps;
    }

    @Override
    public void createModelObjects(TestModels addHere) {
      if (ps != null) {
        addHere.add(new ValidTestModel2(reqs, ps));
      } else {
        addHere.add(new ValidTestModel2(reqs));
      }
    }
  }
}
