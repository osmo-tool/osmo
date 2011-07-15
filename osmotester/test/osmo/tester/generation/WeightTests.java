package osmo.tester.generation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.log.Logger;
import osmo.tester.testmodels.WeightedModel1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Teemu Kanstren
 */
public class WeightTests {
  private OSMOTester osmo = null;
  private TestListener listener;

  @Before
  public void testSetup() {
    Logger.debug = true;
    osmo = new OSMOTester();
    listener = new TestListener();
    osmo.addListener(listener);
  }

  @After
  public void endAssertion() {
  }

  @Test
  public void weightedModel1SuiteSize1() {
    listener.addExpected("suite-start", "start", "t:bob4", "t:bob3", "t:bob2", "t:bob4", "t:bob3", "t:bob4", "t:bob2", "t:bob1", "t:bob4", "t:bob3", "t:bob4", "end", "suite-end");
    osmo.addModelObject(new WeightedModel1());
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    Length length3 = new Length(11);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("Generated sequence for weighted model 1");
  }

  @Test
  public void weightedModel1SuiteSize2() {
    listener.addExpected("suite-start", "start", "t:bob4", "t:bob3", "t:bob2", "t:bob4", "t:bob3", "t:bob4", "end");
    listener.addExpected("start", "t:bob1", "t:bob2", "t:bob3", "t:bob4", "t:bob4", "t:bob3", "end", "suite-end");
    osmo.addModelObject(new WeightedModel1());
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    Length length3 = new Length(6);
    Length length1 = new Length(2);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("Generated sequence 1 for weighted model 1");
  }
}
