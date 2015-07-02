package osmo.tester.examples.tutorial.data;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class HelloModel1 {
  private int helloCount = 0;
  private int worldCount = 0;

  @BeforeTest
  public void startTest() {
    helloCount = 0;
    worldCount = 0;
    System.out.println("TEST START");
  }

  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @Guard("hello")
  public boolean thisNameReallyIsIrrelevant() {
    return helloCount == worldCount;
  }

  @TestStep("hello")
  public void sayHello() {
    String name = "teemu";
    if (Math.random() > 0.5) {
      name = "bob";
    }
    System.out.println("HELLO "+name);
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    String world = "mars";
    if (Math.random() > 0.5) {
      world = "venus";
    }
    System.out.println("WORLD "+world);
    worldCount++;
  }
}
