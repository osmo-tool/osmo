package osmo.examples;

import osmo.common.Randomizer;
import osmo.tester.OSMOTester;
import osmo.tester.annotation.*;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;
import osmo.tester.model.data.ValueSet;
import osmo.tester.reporting.coverage.CSVCoverageReporter;

import java.util.*;

/**
 * @author Teemu Kanstren.
 */
public class VendingMachineWithFails {
  private Scripter scripter = new Scripter();
  @Variable
  private int coins = 0;
  @Variable
  private int bottles = 10;
  private TestSuite testSuite = null;
  private TestCase test = null;
  private Randomizer rand = new Randomizer();
  private final Requirements req = new Requirements();
  private static final String C10 = "10cents";
  private static final String C20 = "20cents";
  private static final String C50 = "50cents";
  private static final String VEND = "vend";
  private Machine machine = new Machine(rand);
  private final int failCoins;
  private final int failBottles;

  public VendingMachineWithFails(int failCoins, int failBottles) {
    req.add(C10);
    req.add(C20);
    req.add(C50);
    req.add(VEND);
    this.failCoins = failCoins;
    this.failBottles = failBottles;
  }

  @Guard("all")
  public boolean gotBottles() {
    return bottles > -1;
  }

  @BeforeTest
  public void start() {
    coins = 0;
    bottles = 10;
    machine.prepare();
    this.test = testSuite.getCurrentTest();
    int tests = testSuite.getAllTestCases().size() + 1;
  }

  @AfterTest
  public void storeTest() {
    test.setAttribute("script", scripter.script);
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
    return machine.canVend(coins);
  }

  @TestStep(VEND)
  public void vend() {
    Item item = machine.random(coins, rand);
    if (coins == failCoins && bottles == failBottles) throw new IllegalArgumentException("SOME PROBLEM");
    scripter.step("VEND (" + bottles + ") - "+ item.name);
    test.addCoverage("item", item.name);
    coins -= item.cost;
    bottles--;
    req.covered(VEND);
    if (bottles == 0) throw new IllegalStateException("ERROR 42345");
  }

  @EndCondition
  public boolean end() {
    return bottles <= 0;
  }

  @Post("all")
  public void checkState() {
    scripter.step("CHECK(bottles == " + bottles + ")");
    scripter.step("CHECK(coins == " + coins + ")");
  }

  private static class Scripter {
    private String script = "";

    public void step(String step) {
      script += step+"\n";
    }
  }

  private static class Machine {
    private final ValueSet<String> items = new ValueSet<>(
            "Banana", "Pepper", "Bell Pepper", "Cucumber", "Daikon", "Carrot", "Turnip", "Parsnip", "Potato", "Corn",
            "Sheep", "Piglet", "Zombie", "Pigman", "Creeper", "Horse", "Dog", "Chicken", "Cat", "Spider", "Skeleton",
            "Milkshake", "Orange Juice", "Apple Juice", "Milk", "Chocolate Milk", "Chocolate bar", "Gummy bears",
            "Coke", "Spite", "Fanta", "Pepsi", "Mountain Dew", "Perrier", "Pellegrino", "Vichy", "Beer", "Wine",
            "Doughnut", "Cinnabon", "Cookie", "Twinkie");
    private final ValueSet<String> properties = new ValueSet<>("Big", "Regular", "Small", "Fluffy", "Spotted",
            "White", "Blue", "Red", "Green", "Yellow", "Orange", "Purple", "Brown", "Turqoise", "Pink");

    private final List<Item> content = new ArrayList<>();
    private final Map<Integer, Integer> itemsForCoins = new LinkedHashMap<>();
    private final Randomizer rand;

    public Machine(Randomizer rand) {
      this.rand = rand;
    }

    public void prepare() {
      long seed = rand.getSeed();
      items.setSeed(seed);
      properties.setSeed(seed);
      createContent();
      Collections.sort(content);
      int coin = 5;
      int index = 1;
      for (Item item : content) {
        while (coin < item.cost) {
          itemsForCoins.put(coin, index);
          coin += 5;
        }
      }
    }

    private void createContent() {
      items.setSeed(rand.getSeed());
      properties.setSeed(rand.getSeed());
      while (content.size() < 100) {
        String property = properties.random();
        String item = items.random();
        String newItem = property + " " + item;
        if (!contains(newItem)) {
          int price = rand.nextInt(1, 50);
          price *= 5;
          put(newItem, price);
        }
      }
    }

    public void put(String name, int cost) {
      content.add(new Item(name, cost));
    }

    public int size() {
      return content.size();
    }

    public Item random(int coins, Randomizer rand) {
      Integer max = itemsForCoins.get(coins);
      if (max == null) max = items.size()-1;
      return content.get(rand.nextInt(0, max));
    }

    public boolean contains(String name) {
      for (Item item : content) {
        if (item.name.equals(name)) return true;
      }
      return false;
    }

    public boolean canVend(int coins) {
      return content.get(0).cost <= coins;
    }
  }

  private static class Item implements Comparable<Item> {
    public final String name;
    public final int cost;

    public Item(String name, int cost) {
      this.name = name;
      this.cost = cost;
    }

    @Override
    public int compareTo(Item o) {
      return o.cost - cost;
    }
  }
}
