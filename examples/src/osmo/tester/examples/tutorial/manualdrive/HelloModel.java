package osmo.tester.examples.tutorial.manualdrive;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.Requirements;
import osmo.tester.model.data.DataGenerationStrategy;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class HelloModel {
  private int helloCount = 0;
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");
  private ValueSet<Integer> sizes = new ValueSet<>(1,2,6);
  private ValueRange<Double> ranges = new ValueRange<>(0.1d, 5.2d);
  @RequirementsField()
  private Requirements reqs = new Requirements();

  @BeforeSuite
  public void init() {
    names.setStrategy(DataGenerationStrategy.BALANCING);
  }

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
    System.out.println("HELLO "+names.next()+" ("+sizes.next()+")");
    helloCount++;
    reqs.covered("Hello");
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD "+worlds.next()+" ("+ranges.next()+")");
    worldCount++;
    reqs.covered("World");
  }
}
