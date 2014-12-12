package osmo.tester.examples.tutorial.basic;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class HelloModel4 {
  private int helloCount = 0;

  @BeforeTest
  public void startTest() {
    helloCount = 0;
    System.out.println("TEST START");
  }

  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO");
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount >= 1;
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD");
  }
}
