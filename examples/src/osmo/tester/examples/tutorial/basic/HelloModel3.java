package osmo.tester.examples.tutorial.basic;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class HelloModel3 {
  @BeforeTest
  public void startTest() {
    System.out.println("TEST START");
  }

  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO");
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD");
  }
}
