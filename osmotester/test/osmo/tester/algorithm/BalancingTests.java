package osmo.tester.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestSequenceListener;
import osmo.tester.generator.algorithm.BalancingAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.ValidTestModel6;
import osmo.tester.testmodels.ValidTestModel7;

import java.util.Collection;
import java.util.logging.Level;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class BalancingTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    OSMOConfiguration.setSeed(100);
    osmo = new OSMOTester();
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @Test
  public void testModel6Length4() {
    listener.addExpected("suite-start", "start", "t:t4", "t:t2", "t:t2", "t:t3", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    Length length4 = new Length(4);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
    listener.validate("Balancing random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new ValidTestModel6());
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Balancing generator should be different from random", random.equals(optimized));
  }

  @Test
  public void testModel6Length20() {
    listener.addExpected("suite-start", "start", "t:t4", "t:t2", "t:t2", "t:t3", "t:t3", "t:t4", "t:t4", "t:t1", "t:t2", "t:t1", "t:t4", "t:t3", "t:t2", "t:t4", "t:t2", "t:t2", "t:t4", "t:t3", "t:t1", "t:t1", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    Length length20 = new Length(20);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length20);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
    listener.validate("Balancing random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new ValidTestModel6());
    osmo.setTestEndCondition(length20);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Balancing generator should be different from random", random.equals(optimized));
  }

  @Test
  public void testModel6Length10000() {
    osmo.addModelObject(new ValidTestModel6());
    Length length1000 = new Length(1000);
    Length length10 = new Length(10);
    osmo.setTestEndCondition(length1000);
    osmo.setSuiteEndCondition(length10);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
  }

  @Test
  public void testModel7Length200() {
//    Logger.consoleLevel = Level.FINE;
    osmo.addModelObject(new ValidTestModel7());
    Length length200 = new Length(200);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length200);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new BalancingAlgorithm());
    osmo.generate();
  }
}
