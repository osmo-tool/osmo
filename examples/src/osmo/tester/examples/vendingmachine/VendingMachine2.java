package osmo.tester.examples.vendingmachine;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;
import osmo.tester.reporting.coverage.CSVCoverageReporter;

import java.io.PrintStream;

import static junit.framework.Assert.*;

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
public class VendingMachine2 {
  private final Scripter scripter;
  public final PrintStream out;
  private int coins = 0;
  private int bottles = 10;
  private TestSuite testSuite = null;
  private final Requirements req = new Requirements();
  private static final String C10 = "10cents";
  private static final String C20 = "20cents";
  private static final String C50 = "50cents";
  private static final String VEND = "vend";

  public VendingMachine2() {
    this(System.out);
  }

  public VendingMachine2(PrintStream ps) {
    scripter = new Scripter(ps);
    this.out = ps;
    req.add(C10);
    req.add(C20);
    req.add(C50);
    req.add(VEND);
  }

  @Guard
  public boolean gotBottles() {
    return bottles > 0;
  }

  @BeforeTest
  public void start() {
    coins = 0;
    bottles = 10;
    int tests = testSuite.getFinishedTestCases().size() + 1;
    out.print("Starting test:" + tests + "\n");
  }

  @AfterSuite
  public void done() {
    out.print("Created total of " + testSuite.getFinishedTestCases().size() + " tests." + "\n");
  }

  @Transition(C20)
  public void insert20cents() {
    scripter.step("20c");
    coins += 20;
    req.covered(C20);
  }

  @Guard({C10, C20, C50})
  public boolean allowCoins() {
    return coins < 300;
  }

  @Transition(C10)
  public void insert10cents() {
    scripter.step("10c");
    coins += 10;
    req.covered(C10);
  }

  @Transition(C50)
  public void insert50cents() {
    scripter.step("50c");
    coins += 50;
    req.covered(C50);
  }

  @Guard(VEND)
  public boolean allowVend() {
    return coins >= 100;
  }

  @Transition(VEND)
  public void vend() {
    scripter.step("VEND (" + bottles + ")");
    coins -= 100;
    bottles--;
    req.covered(VEND);
  }

  @EndCondition
  public boolean end() {
    return bottles <= 0;
  }

  @Post
  public void checkState() {
    scripter.step("CHECK(bottles == " + bottles + ")");
    scripter.step("CHECK(coins == " + coins + ")");
    assertTrue(coins <= 400);
    assertTrue(coins >= 0);
    assertTrue(bottles >= 0);
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(VendingMachine2.class));
    tester.generate(25);

    //Print coverage metric
    CSVCoverageReporter csv = new CSVCoverageReporter(tester.getSuite(), tester.getFsm());
    System.out.println("\n" + csv.getStepCounts());
  }
}
