package osmo.tester.examples.tutorial.scenario;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class HelloModel {
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");

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
    System.out.println("HELLO "+names.random());
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD "+worlds.random());
  }
}
