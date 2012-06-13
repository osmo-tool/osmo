package osmo.tester.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.testmodels.CalculatorModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class TimedTests {
  private CalculatorModel calculator;
  private OSMOTester tester;
  
  @Before
  public void setup() {
    calculator = new CalculatorModel();
    tester = new OSMOTester(calculator);
    tester.setSeed(333);
  }

  @Test
  public void sameMillisForTestAndSuite() {
    tester.addTestEndCondition(new Time(1000, TimeUnit.MILLISECONDS));
    //it is not that predictable that we could do 1000+1000
    tester.addSuiteEndCondition(new Time(800, TimeUnit.MILLISECONDS));
//    tester.addTestEndCondition(new TimedEndCondition(500, TimeUnit.MILLISECONDS));
    long start = System.currentTimeMillis();
    tester.generate();
    long diff = System.currentTimeMillis() - start;
    assertTrue("Time taken with 1000 millisecond end condition should be little over 1000, was "+diff, 1000 <= diff && diff <= 1200);
    TestSuite suite = calculator.getHistory();
    List<TestCase> history = suite.getFinishedTestCases();
    assertEquals("Number of tests generated should be 1", 1, history.size());
  }

  @Test
  public void shorterTimeForSuite() {
    tester.addTestEndCondition(new Time(1000, TimeUnit.MILLISECONDS));
    tester.addSuiteEndCondition(new Time(500, TimeUnit.MILLISECONDS));
    long start = System.currentTimeMillis();
    tester.generate();
    long diff = System.currentTimeMillis() - start;
    //the suite can only end after a test has ended, so we still should get 1000 millis
    assertTrue("Time taken with 1000 millisecond end condition should be close to 1000, was "+diff, 900 <= diff && diff <= 1100);
    TestSuite suite = calculator.getHistory();
    List<TestCase> history = suite.getFinishedTestCases();
    assertEquals("Number of tests generated should be 1", 1, history.size());
  }

  @Test
  public void longerTimeForSuite() {
    tester.addTestEndCondition(new Time(1000, TimeUnit.MILLISECONDS));
    tester.addSuiteEndCondition(new Time(1500, TimeUnit.MILLISECONDS));
    long start = System.currentTimeMillis();
    tester.generate();
    long diff = System.currentTimeMillis() - start;
    //the suite can only end after a test has ended, so we still should get 1000 millis
    assertTrue("Time taken with 1000+1500 millisecond end condition should be little over 2000, was "+diff, 2000 <= diff && diff <= 2200);
    TestSuite suite = calculator.getHistory();
    List<TestCase> history = suite.getFinishedTestCases();
    assertEquals("Number of tests generated", 2, history.size());
  }

  @Test
  public void seconds() {
    tester.addTestEndCondition(new Time(1, TimeUnit.SECONDS));
    tester.addSuiteEndCondition(new Time(2, TimeUnit.SECONDS));
    long start = System.currentTimeMillis();
    tester.generate();
    long diff = System.currentTimeMillis() - start;
    //the suite can only end after a test has ended, so we still should get 1000 millis
    assertTrue("Time taken with 1+2 second end condition should be close to 2000, was "+diff, 1900 <= diff && diff <= 2200);
    TestSuite suite = calculator.getHistory();
    List<TestCase> history = suite.getFinishedTestCases();
    assertEquals("Number of tests generated", 2, history.size());
  }

  @Test
  public void negativeTime() {
    try {
      tester.addTestEndCondition(new Time(-1, TimeUnit.SECONDS));
    } catch (IllegalArgumentException e) {
      assertTrue("Error message should have bad value embedded (-1):"+e.getMessage(), e.getMessage().contains("-1"));
    }
    try {
      tester.addSuiteEndCondition(new Time(-2, TimeUnit.SECONDS));
    } catch (Exception e) {
      assertTrue("Error message should have bad value embedded (-2):" + e.getMessage(), e.getMessage().contains("-2"));
    }
  }
  
}
