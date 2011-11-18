package osmo.tester.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestListener;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.ValidTestModel6;
import osmo.tester.testmodels.WeightedModel1;

import java.util.Collection;

import static junit.framework.Assert.assertFalse;

/** @author Teemu Kanstren */
public class WeightedRandomTests {
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
  public void length4() {
    listener.addExpected("suite-start", "start", "t:bob3", "t:bob1", "t:bob3", "t:bob4", "end", "suite-end");
    osmo.addModelObject(new WeightedModel1());
    Length length4 = new Length(4);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate();
    listener.validate("Optimized random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Optimized generator should be different from random", random.equals(optimized));
  }

  @Test
  public void length20() {
    listener.addExpected("suite-start", "start", "t:bob3", "t:bob1", "t:bob3", "t:bob4", "t:bob2", "t:bob4", "t:bob4", "t:bob4", "t:bob3", "t:bob3", "t:bob2", "t:bob4", "t:bob4", "t:bob4", "t:bob2", "t:bob4", "t:bob1", "t:bob4", "t:bob3", "t:bob4", "end", "suite-end");
    osmo.addModelObject(new WeightedModel1());
    Length length15 = new Length(20);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length15);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate();
    listener.validate("Optimized random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length15);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Optimized generator should be different from random", random.equals(optimized));
  }
}
