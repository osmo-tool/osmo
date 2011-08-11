package osmo.tester.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestListener;
import osmo.tester.generator.algorithm.OptimizedRandomAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.ValidTestModel6;

import java.util.Collection;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * @author Teemu Kanstren
 */
public class OptimizedRandomTests {
  private OSMOTester osmo = null;
  private TestListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setRandom(new Random(100));
    listener = new TestListener();
    osmo.addListener(listener);
  }

  @Test
  public void testModel6Length4() {
    listener.addExpected("suite-start", "start", "t:t1", "t:t2", "t:t1", "t:t4", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    Length length4 = new Length(4);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new OptimizedRandomAlgorithm());
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
    listener.addExpected("suite-start", "start", "t:t1", "t:t2", "t:t1", "t:t4", "t:t1", "t:t3", "t:t2", "t:t2", "t:t4", "t:t2", "t:t3", "t:t4", "t:t4", "t:t3", "t:t3", "t:t1", "t:t1", "t:t1", "t:t3", "t:t3", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    Length length15 = new Length(20);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length15);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new OptimizedRandomAlgorithm());
    osmo.generate();
    listener.validate("Optimized random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new ValidTestModel6());
    osmo.addTestEndCondition(length15);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Optimized generator should be different from random", random.equals(optimized));
  }
}
