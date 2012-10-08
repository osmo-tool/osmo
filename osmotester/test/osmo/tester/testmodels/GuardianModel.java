package osmo.tester.testmodels;

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
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * A test model with guard variants.
 *
 * @author Teemu Kanstren
 */
public class GuardianModel {
  private final PrintStream out;
  private boolean epix = false;

  public GuardianModel(PrintStream out) {
    this.out = out;
  }

  @Guard("hello")
  public boolean helloCheck() {
    out.print(":g-hello");
    return true;
  }

  @Transition("hello")
  public void transition1() {
    out.print(":hello");
  }

  @Guard("world")
  public boolean worldCheck() {
    out.print(":g-world");
    return true;
  }

  @Transition("world")
  public void epix() {
    out.print(":world");
  }

  @Guard("epixx")
  public boolean kitty() {
    out.print(":g-epix");
    return !epix;
  }

  @Guard("!epixx")
  public boolean kitted() {
    out.print(":g-!epix");
    return epix;
  }

  @Pre("epixx")
  public void epixxPre() {
    out.print(":epixx_pre");
  }

  @Transition("epixx")
  public void epixx() {
    out.print(":epixx");
    epix = true;
  }

  @Post("epixx")
  public void epixxO() {
    out.print(":epixx_oracle");
  }

  @Post
  public void stateCheck() {
    out.print(":gen_oracle");
  }
}
