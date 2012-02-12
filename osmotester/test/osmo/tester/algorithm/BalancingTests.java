package osmo.tester.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestListener;
import osmo.tester.generator.algorithm.BalancingAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.ValidTestModel6;
import osmo.tester.testmodels.ValidTestModel7;

import java.util.Collection;

import static junit.framework.Assert.assertFalse;

/** @author Teemu Kanstren */
public class BalancingTests {
  private OSMOTester osmo = null;
  private TestListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(100);
    listener = new TestListener();
    osmo.addListener(listener);
  }

  @Test
  public void testModel6Length4() {
    listener.addExpected("suite-start", "start", "t:t1", "t:t1", "t:t2", "t:t1", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    Length length4 = new Length(4);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
    listener.validate("Optimized random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new ValidTestModel6());
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Optimized generator should be different from random", random.equals(optimized));
  }

  @Test
  public void testModel6Length20() {
    listener.addExpected("suite-start", "start", "t:t1", "t:t1", "t:t2", "t:t1", "t:t4", "t:t3", "t:t4", "t:t4", "t:t1", "t:t3", "t:t2", "t:t4", "t:t2", "t:t3", "t:t3", "t:t1", "t:t1", "t:t3", "t:t1", "t:t2", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    Length length20 = new Length(20);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length20);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
    listener.validate("Optimized random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new ValidTestModel6());
    osmo.addTestEndCondition(length20);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Optimized generator should be different from random", random.equals(optimized));
  }

  @Test
  public void testModel6Length10000() {
    osmo.addModelObject(new ValidTestModel6());
    Length length1000 = new Length(1000);
    Length length10 = new Length(10);
    osmo.addTestEndCondition(length1000);
    osmo.addSuiteEndCondition(length10);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
  }

  @Test
  public void testModel7Length200() {
//    Logger.debug = true;
    osmo.addModelObject(new ValidTestModel7());
    Length length200 = new Length(200);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length200);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
  }
}
