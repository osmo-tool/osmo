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

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class WeightedDistributionTests {
  private OSMOTester osmo = null;
  private TestDistributionListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(100);
    listener = new TestDistributionListener();
    osmo.addListener(listener);
  }

  @Test
  public void randomLength1000() {
    listener.setExpected("bob1", 109);
    listener.setExpected("bob2", 191);
    listener.setExpected("bob3", 296);
    listener.setExpected("bob4", 404);
    osmo.addModelObject(new WeightedModel1());
    Length length4 = new Length(1000);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate();
    listener.validate("Weighted random generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(balanced));
  }

  @Test
  public void balancingLength1000() {
    listener.setExpected("bob1", 157);
    listener.setExpected("bob2", 240);
    listener.setExpected("bob3", 267);
    listener.setExpected("bob4", 336);
    osmo.addModelObject(new WeightedModel1());
    Length length4 = new Length(1000);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    osmo.generate();
    listener.validate("Weighted balancing generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted balancing should be different from random", random.equals(balanced));
  }

  @Test
  public void randomLength200Suite6() {
    listener.setExpected("bob1", 125);
    listener.setExpected("bob2", 235);
    listener.setExpected("bob3", 360);
    listener.setExpected("bob4", 480);
    osmo.addModelObject(new WeightedModel1());
    Length length4 = new Length(200);
    Length length1 = new Length(6);
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate();
    listener.validate("Weighted balancing generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(balanced));
  }

  @Test
  public void balancingLength200Suite6() {
    listener.setExpected("bob1", 190);
    listener.setExpected("bob2", 275);
    listener.setExpected("bob3", 323);
    listener.setExpected("bob4", 412);
    osmo.addModelObject(new WeightedModel1());
    Length length4 = new Length(200);
    Length length1 = new Length(6);
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    osmo.generate();
    listener.validate("Weighted balancing generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.addModelObject(new WeightedModel1());
    osmo.addTestEndCondition(length4);
    osmo.addSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(balanced));
  }
}
