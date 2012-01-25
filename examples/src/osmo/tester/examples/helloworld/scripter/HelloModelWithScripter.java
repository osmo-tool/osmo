package osmo.tester.examples.helloworld.scripter;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

/** @author Teemu Kanstren */
public class HelloModelWithScripter {
  private int helloCount = 0;
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<String>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<String>("mars", "venus");
  private ValueSet<Integer> sizes = new ValueSet<Integer>(1,2,6);
  private ValueRange<Double> ranges = new ValueRange<Double>(0.1d, 5.2d);
  private final HelloScripter scripter;

  public HelloModelWithScripter(HelloScripter scripter) {
    this.scripter = scripter;
  }

  @BeforeSuite
  public void init() {
    names.setStrategy(DataGenerationStrategy.BALANCING);
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
    scripter.hello(names.next(), sizes.next());
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    scripter.world(worlds.next(), ranges.next());
    worldCount++;
  }
}
