package osmo.tester.examples.vendingmachine;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;

import java.io.PrintStream;

import static junit.framework.Assert.assertTrue;

/**
 * Example of a vending machine.
 * Takes 10, 20, and 50 cent coins.
 * Maximum of 100 cents allowed to be inserted.
 * If inserting a coin would go over total of 100 cents, it is not allowed.
 * When 100 cents inserted the "vend" transition can be taken.
 * When "vend" is taken, number of coins is reset to 0 and a bottle is deducted from the number of available bottles.
 * When there are only 0 bottles left, all states are disabled.
 * 
 * @author Teemu Kanstren
 */
public class VendingExample {
  private final Scripter scripter;
  private final PrintStream out;
  private int coins = 0;
  private int bottles = 10;
  @TestSuiteField
  private TestSuite testSuite = new TestSuite();

  public VendingExample() {
    scripter = new Scripter(System.out);
    this.out = System.out;
  }

  public VendingExample(PrintStream ps) {
    scripter = new Scripter(ps);
    this.out = ps;
  }

  @Guard
  public boolean gotBottles() {
    return bottles > 0;
  }

  @BeforeTest
  public void start() {
    coins = 0;
    //uncomment this for failure to continue with 0 available transitions
    bottles = 10;
    int tests = testSuite.getTestCases().size()+1;
    out.println("Starting test:"+ tests);
  }

  @AfterSuite
  public void done() {
    out.println("Created total of "+ testSuite.getTestCases().size()+" tests.");
  }

  @Guard("20cents")
  public boolean allow20cents() {
    return coins <= 80;
  }

  @Transition("20cents")
  public void insert20cents() {
    scripter.step("INSERT 20");
    coins += 20;
  }

  @Guard("10cents")
  public boolean allow10cents() {
    return coins <= 90;
  }

  @Transition("10cents")
  public void insert10cents() {
    scripter.step("INSERT 10");
    coins += 10;
  }

  @Guard("50cents")
  public boolean allow50cents() {
    return coins <= 50;
  }

  @Transition("50cents")
  public void insert50cents() {
    scripter.step("INSERT 50");
    coins += 50;
  }

  @Guard("vend")
  public boolean allowVend() {
    return coins == 100;
  }

  @Transition("vend")
  public void vend() {
    scripter.step("VEND ("+bottles+")");
    coins = 0;
    bottles--;
  }

  @EndCondition
  public boolean end() {
    return bottles <= 0;
  }

  @Post
  public void checkState() {
    scripter.step("CHECK(bottles == "+bottles+")");
    scripter.step("CHECK(coins == "+coins+")");
    assertTrue(coins <= 100);
    assertTrue(coins >= 0);
    assertTrue(bottles >= 0);
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new VendingExample());
//    tester.setDebug(true);
    tester.generate();
  }
}
