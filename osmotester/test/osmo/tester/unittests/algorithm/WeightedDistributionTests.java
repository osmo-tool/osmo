package osmo.tester.unittests.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedBalancingAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.unittests.generation.TestDistributionListener;
import osmo.tester.unittests.testmodels.WeightedModel1;

import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class WeightedDistributionTests {
  private OSMOTester osmo = null;
  private TestDistributionListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    listener = new TestDistributionListener();
    osmo.addListener(listener);
  }

  @Test
  public void randomLength1000() {
    listener.setExpected("bob1", 108);
    listener.setExpected("bob2", 191);
    listener.setExpected("bob3", 298);
    listener.setExpected("bob4", 403);
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    Length length4 = new Length(1000);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate(100);
    listener.validate("Weighted random generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate(100);
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(balanced));
  }

  @Test
  public void balancingLength1000() {
    listener.setExpected("bob1", 1622);
    listener.setExpected("bob2", 2290);
    listener.setExpected("bob3", 2799);
    listener.setExpected("bob4", 3289);
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    Length length4 = new Length(10000);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    osmo.generate(100);
    listener.validate("Weighted balancing generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate(100);
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted balancing should be different from random", random.equals(balanced));
  }

  @Test
  public void randomLength200Suite6() {
    listener.setExpected("bob1", 125);
    listener.setExpected("bob2", 236);
    listener.setExpected("bob3", 326);
    listener.setExpected("bob4", 513);
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    Length length4 = new Length(200);
    Length length1 = new Length(6);
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedRandomAlgorithm());
    osmo.generate(100);
    listener.validate("Weighted balancing generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate(100);
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(balanced));
  }

  @Test
  public void balancingLength200Suite6() {
    listener.setExpected("bob1", 205);
    listener.setExpected("bob2", 269);
    listener.setExpected("bob3", 320);
    listener.setExpected("bob4", 406);
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    Length length4 = new Length(200);
    Length length1 = new Length(6);
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new WeightedBalancingAlgorithm());
    osmo.generate(100);
    listener.validate("Weighted balancing generator steps");
    Map<String, Integer> balanced = listener.getSteps();

    testSetup();
    osmo.setModelFactory(new ReflectiveModelFactory(WeightedModel1.class));
    osmo.setTestEndCondition(length4);
    osmo.setSuiteEndCondition(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate(100);
    Map<String, Integer> random = listener.getSteps();
    assertFalse("Weighted generator should be different from random", random.equals(balanced));
  }
}
