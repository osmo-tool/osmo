package osmo.tester.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestDistributionListener;
import osmo.tester.generation.TestSequenceListener;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedBalancingAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.WeightedModel1;

import java.util.Collection;
import java.util.Map;

import static junit.framework.Assert.assertFalse;

/** @author Teemu Kanstren */
public class WeightedSequenceTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(100);
    listener = new TestSequenceListener();
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
    listener.validate("Weighted random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(optimized));
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
    listener.validate("Weighted random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length15);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(optimized));
  }
}
