package osmo.tester.examples.tutorial.modularization;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class HelloModularModel {
  private ModelState state = new ModelState();

  @BeforeTest
  public void startTest() {
    state.reset();
    System.out.println("TEST START");
  }

  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @Guard("hello")
  public boolean thisNameReallyIsIrrelevant() {
    return state.canHello();
  }

  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO "+state.nextName()+"("+state.nextSize()+")");
    state.didHello();
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return state.canWorld();
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD "+state.nextWorld()+"("+state.nextRange()+")");
    state.didWorld();
  }
}
