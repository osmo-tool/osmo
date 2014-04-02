package osmo.tester.examples.tutorial.modularization;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class WorldModule {
  private final ModelState state;

  public WorldModule(ModelState state) {
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
