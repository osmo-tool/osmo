package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.examples.calculator.CalculatorModel2;
import osmo.tester.examples.vendingmachine.VendingExample;
import osmo.tester.generator.endcondition.Length;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import static junit.framework.Assert.assertEquals;

/**
 * Test cases to verify the example models in osmo.tester.examples package.
 *
 * @author Teemu Kanstren
 */
public class ExampleTests {
  private static final String ln = System.getProperty("line.separator");
  private OSMOTester osmo = new OSMOTester();

  @Before
  public void setUp() {
    osmo.setRandom(new Random(100));
  }

  @Test
  public void testCalculatorModel1() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new CalculatorModel(ps));
    Length length3 = new Length(3);
    Length length2 = new Length(2);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length2);
    osmo.generate();
    String expected = "first" + ln +
            "Starting new test case 1" + ln +
            "S:0" + ln +
            "+ 2" + ln +
            "+ 3" + ln +
            "Test case ended" + ln +
            "Starting new test case 2" + ln +
            "S:0" + ln +
            "+ 2" + ln +
            "+ 3" + ln +
            "Test case ended" + ln +
            "last"+ln;
    String actual = out.toString();
    assertEquals(expected, actual);
  }

  @Test
  public void testCalculatorModel2() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new CalculatorModel2(ps));
    Length length3 = new Length(3);
    Length length2 = new Length(2);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length2);
    osmo.generate();
    String expected = "first" + ln +
            "Starting new test case 1" + ln +
            "S:0" + ln +
            "Decreased: 1" + ln +
            "Increased: 0" + ln +
            "Test case ended" + ln +
            "Starting new test case 2" + ln +
            "S:0" + ln +
            "Decreased: 1" + ln +
            "Increased: 0" + ln +
            "Test case ended" + ln +
            "last" + ln;
    String actual = out.toString();
    assertEquals(expected, actual);
  }

  @Test
  public void testVendingExample() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new VendingExample(ps));
    Length length3 = new Length(3);
    Length length2 = new Length(2);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length2);
    osmo.generate();
    String expected = "Starting test:1\n" +
            "INSERT 20\n" +
            "CHECK(bottles == 10)\n" +
            "CHECK(coins == 20)\n" +
            "INSERT 10\n" +
            "CHECK(bottles == 10)\n" +
            "CHECK(coins == 30)\n" +
            "INSERT 10\n" +
            "CHECK(bottles == 10)\n" +
            "CHECK(coins == 40)\n" +
            "Starting test:2\n" +
            "INSERT 50\n" +
            "CHECK(bottles == 10)\n" +
            "CHECK(coins == 50)\n" +
            "INSERT 20\n" +
            "CHECK(bottles == 10)\n" +
            "CHECK(coins == 70)\n" +
            "INSERT 50\n" +
            "CHECK(bottles == 10)\n" +
            "CHECK(coins == 120)\n" +
            "Created total of 2 tests.\n";
    String actual = out.toString();
    assertEquals(expected, actual);
  }
}
