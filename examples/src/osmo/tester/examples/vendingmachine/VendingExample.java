package osmo.tester.examples.vendingmachine;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.Input;
import osmo.tester.model.data.ValueSet;

import java.io.PrintStream;

import static junit.framework.Assert.*;

/**
 * Example of a vending machine.
 * Takes 10, 20, and 50 cent coins.
 * Price is configured with a variable and can be changed to create a new test set with changed price.
 * When 100 cents (or the value of the price variable if changed) inserted the "vend" transition can be taken.
 * When "vend" is taken, number of coins is reduced by the price value and a bottle is deducted from the number of available bottles.
 * When there are only 0 bottles left, the model end condition ends test generation.
 *
 * @author Teemu Kanstren
 */
public class VendingExample {
  private final Scripter scripter;
  private final PrintStream out;
  private int cents = 0;
  private int bottles = 10;
  private final int PRICE = 100;
  private final Input<Integer> coins = new ValueSet<>(10, 20, 50);
  private TestSuite testSuite = null;

  public VendingExample() {
    scripter = new Scripter(System.out);
    this.out = System.out;
  }

  public VendingExample(PrintStream ps) {
    scripter = new Scripter(ps);
    this.out = ps;
  }

  @BeforeTest
  public void start() {
    cents = 0;
    //uncomment this for failure to continue with 0 available transitions
    bottles = 10;
    int tests = testSuite.getFinishedTestCases().size() + 1;
    out.print("Starting test:" + tests + "\n");
  }

  @AfterSuite
  public void done() {
    out.print("Created total of " + testSuite.getFinishedTestCases().size() + " tests.\n");
  }

  @Transition("insert-money")
  public void insertMoney() {
    int coin = coins.next();
    scripter.step("INSERT " + coin);
    cents += coin;
  }

  @Guard("vend")
  public boolean allowVend() {
    return cents >= PRICE;
  }

  @Transition("vend")
  public void vend() {
    scripter.step("VEND (" + bottles + ")");
    cents -= PRICE;
    bottles--;
  }

  @EndCondition
  public boolean end() {
    return bottles <= 0;
  }

  @Pre
  public void hello() {
    String name = testSuite.getCurrentTest().getCurrentStep().getName();
    System.out.println("NAME-PRE:"+name);
  }

  @Post
  public void checkState() {
    scripter.step("CHECK(bottles == " + bottles + ")");
    scripter.step("CHECK(coins == " + cents + ")");
    assertTrue(cents >= 0);
    assertTrue(bottles >= 0);
    String name = testSuite.getCurrentTest().getCurrentStep().getName();
    System.out.println("NAME-POST:"+name);
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new VendingExample());
    tester.generate(100);
  }
}
