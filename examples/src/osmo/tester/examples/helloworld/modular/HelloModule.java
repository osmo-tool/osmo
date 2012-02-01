package osmo.tester.examples.helloworld.modular;

import osmo.tester.annotation.*;

/** @author Teemu Kanstren */
public class HelloModule {
  private final SeparateState state;

  public HelloModule(SeparateState state) {
    this.state = state;
  }

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
    System.out.println("HELLO "+state.nextName()+" ("+state.nextSize()+")");
    state.didHello();
  }
}
