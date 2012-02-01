package osmo.tester.examples.helloworld.modular;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.examples.helloworld.modular.SeparateState;

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
