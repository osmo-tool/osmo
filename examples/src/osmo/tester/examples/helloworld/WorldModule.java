package osmo.tester.examples.helloworld;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class WorldModule {
  private final SeparateState state;

  public WorldModule(SeparateState state) {
    this.state = state;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return state.canWorld();
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD " + state.nextWorld() + " (" + state.nextRange() + ")");
    state.didWorld();
  }
}
