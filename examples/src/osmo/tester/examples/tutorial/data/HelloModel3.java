package osmo.tester.examples.tutorial.data;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.data.Text;

/** @author Teemu Kanstren */
public class HelloModel3 {
  private int helloCount = 0;
  private int worldCount = 0;
  private Text text = new Text(3, 7);
  
  @BeforeSuite
  public void init() {
    text.asciiLettersAndNumbersOnly();  }

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
    System.out.println("HELLO "+text.random());
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD "+text.random());
    worldCount++;
  }
}
