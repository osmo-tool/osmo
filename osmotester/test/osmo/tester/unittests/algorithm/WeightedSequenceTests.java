package osmo.tester.unittests.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.unittests.generation.TestSequenceListener;
import osmo.tester.unittests.testmodels.WeightedModel1;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class WeightedSequenceTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @Test
  public void length4() {
    listener.addExpected("suite-start", "start", "t:bob2", "t:bob4", "t:bob3", "t:bob4", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    Length length4 = new Length(4);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate(1001);
    listener.validate("Weighted random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate(100);
    Collection<String> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(optimized));
  }

  @Test
  public void length20() {
    String[] expected = {"suite-start", "start", "t:bob2", "t:bob4", "t:bob3", "t:bob4", "t:bob1", "t:bob2", "t:bob3", "t:bob2", "t:bob3", "t:bob2", "t:bob3", "t:bob1", "t:bob3", "t:bob2", "t:bob1", "t:bob3", "t:bob3", "t:bob1", "t:bob3", "t:bob4", "end", "suite-end"};
    listener.addExpected(expected);
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    Length length15 = new Length(20);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length15);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate(1001);
    listener.validate("Weighted random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setTestEndCondition(length15);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate(1001);
    Collection<String> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(optimized));
  }
}
