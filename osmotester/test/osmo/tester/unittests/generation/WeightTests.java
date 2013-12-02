package osmo.tester.unittests.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.algorithm.WeightedBalancingAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.unittests.testmodels.WeightedModel1;

/** @author Teemu Kanstren */
public class WeightTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @After
  public void endAssertion() {
  }

  @Test
  public void weightedModel1SuiteSize1() {
    listener.addExpected("suite-start", "start", "t:bob3", "t:bob3", "t:bob4", "t:bob4", "t:bob2", "t:bob3", "t:bob2", "t:bob1", "t:bob1", "t:bob3", "t:bob2", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    Length length3 = new Length(11);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(100);
    listener.validate("Generated sequence for weighted model 1");
  }

  @Test
  public void weightedModel1SuiteSize2() {
    listener.addExpected("suite-start", "start", "t:bob3", "t:bob3", "t:bob4", "t:bob4", "t:bob2", "t:bob3", "end");
    listener.addExpected("start", "t:bob2", "t:bob1", "t:bob1", "t:bob3", "t:bob2", "t:bob4", "end", "suite-end");
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    Length length3 = new Length(6);
    Length length1 = new Length(2);
    osmo.setTestEndCondition(length3);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(100);
    listener.validate("Generated sequence 2 for weighted model 1");
  }
}
