package osmo.tester.examples.helloworld.online;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class OnlineHelloModel {
  private int helloCount = 0;
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");
  private ValueSet<Integer> sizes = new ValueSet<>(1, 2, 6);
  private ValueRange<Double> ranges = new ValueRange<>(0.1d, 5.2d);
  private HelloProgram sut = new HelloProgram();

  @BeforeSuite
  public void init() {
    names.setStrategy(DataGenerationStrategy.BALANCING);
  }

  @BeforeTest
  public void startTest() {
    //this is where you would actually set up your test objects to get independent tests
//    sut = new HelloProgram();
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
    String name = names.next();
    String response = sut.hello(name, sizes.next());
    assertEquals("hi dude, " + name, response);
    System.out.println("HELLO");
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    double range = ranges.next();
    String response = sut.world(worlds.next(), range);
    assertEquals(range + "? thats pretty mighty, dude", response);
    System.out.println("WORLD");
    worldCount++;
  }
}
