package osmo.tester.examples.tutorial.basic;

import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class HelloModel1 {
  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO");
  }
}
