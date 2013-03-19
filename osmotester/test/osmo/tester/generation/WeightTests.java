package osmo.tester.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.algorithm.WeightedBalancingAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.testmodels.WeightedModel1;

/** @author Teemu Kanstren */
public class WeightTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(100);
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @After
  public void endAssertion() {
  }

  @Test
  public void weightedModel1SuiteSize1() {
    listener.addExpected("suite-start", "start", "t:bob2", "t:bob3", "t:bob4", "t:bob4", "t:bob4", "t:bob3", "t:bob2", "t:bob3", "t:bob2", "t:bob3", "t:bob1", "end", "suite-end");
    osmo.addModelObject(new WeightedModel1());
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    Length length3 = new Length(11);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("Generated sequence for weighted model 1");
  }

  @Test
  public void weightedModel1SuiteSize2() {
    listener.addExpected("suite-start", "start", "t:bob2", "t:bob3", "t:bob4", "t:bob4", "t:bob4", "t:bob3", "end");
    listener.addExpected("start", "t:bob2", "t:bob3", "t:bob2", "t:bob3", "t:bob1", "t:bob2", "end", "suite-end");
    osmo.addModelObject(new WeightedModel1());
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    Length length3 = new Length(6);
    Length length1 = new Length(2);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("Generated sequence 2 for weighted model 1");
  }
}
