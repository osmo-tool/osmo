package osmo.tester.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestListener;
import osmo.tester.generator.algorithm.OptimizedRandomAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.strategy.LengthStrategy;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel2;
import osmo.tester.testmodels.ValidTestModel6;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;

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
    listener = new TestListener();
    osmo.addListener(listener);
  }

  @Test
  public void testModel6Length4() {
    listener.addExpected("suite-start", "start", "t:t1", "t:t3", "t:t4", "t:t2", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    LengthStrategy length4 = new LengthStrategy(4);
    LengthStrategy length1 = new LengthStrategy(1);
    osmo.setTestStrategy(length4);
    osmo.setSuiteStrategy(length1);
    osmo.setAlgorithm(new OptimizedRandomAlgorithm());
    osmo.generate();
    listener.validate("Optimized random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new ValidTestModel6());
    osmo.setTestStrategy(length4);
    osmo.setSuiteStrategy(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Optimized generator should be different from random", random.equals(optimized));
  }

  @Test
  public void testModel6Length15() {
    listener.addExpected("suite-start", "start", "t:t1", "t:t2", "t:t3", "t:t4", "t:t1", "t:t2", "t:t3", "t:t4", "t:t4", "t:t2", "t:t3", "t:t1", "t:t1", "t:t4", "t:t3", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel6());
    LengthStrategy length15 = new LengthStrategy(15);
    LengthStrategy length1 = new LengthStrategy(1);
    osmo.setTestStrategy(length15);
    osmo.setSuiteStrategy(length1);
    osmo.setAlgorithm(new OptimizedRandomAlgorithm());
    osmo.generate();
    listener.validate("Optimized random generator steps");
    Collection<String> optimized = listener.getSteps();

    testSetup();
    osmo.addModelObject(new ValidTestModel6());
    osmo.setTestStrategy(length15);
    osmo.setSuiteStrategy(length1);
    osmo.setAlgorithm(new RandomAlgorithm());
    osmo.generate();
    Collection<String> random = listener.getSteps();
    assertFalse("Optimized generator should be different from random", random.equals(optimized));
  }
}
