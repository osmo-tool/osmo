package osmo.tester.examples.tutorial.online;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class HelloModel1 {
  private int helloCount = 0;
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");
  private ValueSet<Integer> sizes = new ValueSet<>(1,2,6);
  private ValueRange<Double> ranges = new ValueRange<>(0.1d, 5.2d);
  private HelloProgram1 sut = new HelloProgram1();
  
  @BeforeSuite
  public void init() {
    
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
    String name = names.balanced();
    String response = sut.hello(name, sizes.random());
    assertEquals("hi dude, " + name, response);
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    double range = ranges.random();
    String response = sut.world(worlds.random(), range);
    assertEquals(range+"? swweeet, dude", response);
    worldCount++;
  }
}
