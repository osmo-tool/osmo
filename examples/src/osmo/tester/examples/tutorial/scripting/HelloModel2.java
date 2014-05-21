package osmo.tester.examples.tutorial.scripting;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class HelloModel2 {
  private int helloCount = 0;
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");
  private ValueSet<Integer> sizes = new ValueSet<>(1,2,6);
  private ValueRange<Double> ranges = new ValueRange<>(0.1d, 5.2d);
  private final HelloScripterI scripter;

  public HelloModel2(HelloScripterI scripter) {
    this.scripter = scripter;
  }

  @BeforeSuite
  public void init() {
    
  }

  @BeforeTest
  public void startTest() {
    helloCount = 0;
    worldCount = 0;
    scripter.startTest();
  }

  @AfterTest
  public void endTest() {
    scripter.endTest();
  }

  @Guard("hello")
  public boolean thisNameReallyIsIrrelevant() {
    return helloCount == worldCount;
  }

  @TestStep("hello")
  public void sayHello() {
    scripter.hello(names.balanced(), sizes.random());
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    scripter.world(worlds.random(), ranges.random());
    worldCount++;
  }
}
