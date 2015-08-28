package osmo.tester.unittests.testmodels;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.*;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;
import osmo.tester.model.data.ValueSet;
import osmo.tester.reporting.coverage.CSVCoverageReporter;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class VendingMachine {
  private Scripter scripter;
  private int coins = 0;
  private int bottles = 10;
  private TestSuite testSuite = null;
  private final Requirements req = new Requirements();
  private static final String C10 = "10cents";
  private static final String C20 = "20cents";
  private static final String C50 = "50cents";
  private static final String VEND = "vend";
  private static final ValueSet<String> items = new ValueSet<>(
          "Banana", "Pepper", "Bell Pepper", "Cucumber", "Daikon", "Carrot", "Turnip", "Parsnip", "Potato", "Corn",
          "Sheep", "Piglet", "Zombie", "Pigman", "Creeper", "Horse", "Dog", "Chicken", "Cat", "Spider", "Skeleton",
          "Milkshake", "Orange Juice", "Apple Juice", "Milk", "Chocolate Milk", "Chocolate bar", "Gummy bears",
          "Coke", "Spite", "Fanta", "Pepsi", "Mountain Dew", "Perrier", "Pellegrino", "Vichy", "Beer", "Wine",
          "Doughnut", "Cinnabon", "Cookie", "Twinkie");
  private static final ValueSet<String> adjectives = new ValueSet<>("Big", "Regular", "Small", "Fluffy", "Spotted",
          "White", "Blue", "Red", "Green", "Yellow", "Orange", "Purple", "Brown", "Turqoise", "Pink");

  public VendingMachine() {
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
    int tests = testSuite.getAllTestCases().size() + 1;
  }

  @AfterSuite
  public void done() {
  }

  @TestStep(C20)
  public void insert20cents() {
    scripter.step("20c");
    coins += 20;
    req.covered(C20);
  }

  @Guard({C10, C20, C50})
  public boolean allowCoins() {
    return coins < 300;
  }

  @TestStep(C10)
  public void insert10cents() {
    scripter.step("10c");
    coins += 10;
    req.covered(C10);
  }

  @TestStep(C50)
  public void insert50cents() {
    scripter.step("50c");
    coins += 50;
    req.covered(C50);
  }

  @Guard(VEND)
  public boolean allowVend() {
    return coins >= 100;
  }

  @TestStep(VEND)
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
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new VendingMachine());
    tester.generate(25);

    //Print coverage metric
    TestSuite suite = tester.getSuite();
    CSVCoverageReporter csv = new CSVCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), tester.getFsm());
    System.out.println("\n" + csv.getStepCounts());
  }

  private static class Scripter {
    private String script = "";

    public void step(String step) {
      script += step+"\n";
    }
  }
}
