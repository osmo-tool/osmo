package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;

import java.io.PrintStream;

/**
 * A test model with guard variants.
 *
 * @author Teemu Kanstren
 */
public class GuardianModel {
  private final PrintStream out;
  private boolean epix = false;
  private String pre = "";

  public GuardianModel(PrintStream out) {
    this.out = out;
  }

  public GuardianModel(PrintStream out, String pre) {
    this.out = out;
    this.pre = pre;
  }

  @Guard("hello")
  public boolean helloCheck() {
    out.print(pre + ":g-hello\n");
    return true;
  }

  @TestStep("hello")
  public void transition1() {
    out.print(pre + ":hello\n");
  }

  @Guard("world")
  public boolean worldCheck() {
    out.print(pre + ":g-world\n");
    return true;
  }

  @TestStep("world")
  public void epix() {
    out.print(pre + ":world\n");
  }

  @Guard("epixx")
  public boolean kitty() {
    out.print(pre + ":g-epix\n");
    return !epix;
  }

  @Guard("!epixx")
  public boolean kitted() {
    out.print(pre + ":g-!epix\n");
    return epix;
  }

  @Pre("epixx")
  public void epixxPre() {
    out.print(pre + ":epixx_pre\n");
  }

  @TestStep("epixx")
  public void epixx() {
    out.print(pre + ":epixx\n");
    epix = true;
  }

  @Post("epixx")
  public void epixxO() {
    out.print(pre + ":epixx_oracle\n");
  }

  @Post("all")
  public void stateCheck() {
    out.print(pre + ":gen_oracle\n");
  }
}
