package osmo.tester.unittests.endconditions;

import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.*;
import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.endcondition.logical.Or;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren.
 */
public class CloningTests {
  @Test
  public void cloneLength() {
    Length ec = new Length(5);
    runN(ec, 10, 20, 11);
  }

  @Test
  public void cloneLengthProbability() {
    EndCondition ec = new LengthProbability(5, 20, 0.4);
    runN(ec, 10, 30, 11);
  }

  @Test
  public void cloneProbability() {
    EndCondition ec = new Probability(0.4);
    runN(ec, 10, 30, 11);
  }

  @Test
  public void cloneWeighted2() {
    EndCondition ec = new WeightedEndConditionSet(10, new Length(5), 10, new Length(5));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void cloneWeighted3() {
    WeightedEndConditionSet ec = new WeightedEndConditionSet(10, new Length(5), 10, new Length(5));
    ec.addEndCondition(20, new Length(15));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void and2() {
    And ec = new And(new Length(10), new Probability(0.5));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void and3() {
    And ec = new And(new Length(10), new Probability(0.5), new Length(15));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void and1() {
    And ec = new And(new Length(10));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void or1() {
    Or ec = new Or(new Length(10));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void or2() {
    Or ec = new Or(new Length(10), new Length(5));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void or3() {
    Or ec = new Or(new Length(10), new Length(5), new Probability(0.4));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void andOr() {
    And ec = new And(new Length(10), new Or(new Length(5), new Probability(0.4)));
    runN(ec, 10, 30, 11);
  }

  @Test
  public void orAnd() {
    Or ec = new Or(new Length(10), new And(new Length(5), new Probability(0.4)));
    runN(ec, 10, 30, 11);
  }

  private void runN(EndCondition ec, int n, int length, int seed) {
    for (int i = 0 ; i < n ; i++) {
      //we reuse the seed for both end conditions to get deterministic comparison
      runBoth(ec, length, seed+i, seed+i);
    }
  }

  private void runBoth(EndCondition ec, int length, int seed1, int seed2) {
    ec.init(seed1, null, new OSMOConfiguration());
    List<Boolean> verdicts = runTest(ec, length);
    EndCondition cloned = ec.cloneMe();
    cloned.init(seed2, null, new OSMOConfiguration());
    List<Boolean> verdicts2 = runTest(cloned, length);
    assertEquals("EC clone performance", verdicts, verdicts2);
  }

  private List<Boolean> runTest(EndCondition ec, int length) {
    List<Boolean> verdicts = new ArrayList<>();
    TestSuite suite = new TestSuite();
    suite.startTest(11);
    for (int i = 0 ; i < 10 ; i++) {
      suite.addStep(new FSMTransition("step"+i));
      verdicts.add(ec.endTest(suite, null));
    }
    return verdicts;
  }

  @Test
  public void cloningTime()throws Exception {
    EndCondition ec = new Time(1000, TimeUnit.MILLISECONDS);
    //TODO: does not actually test parallel runs
    runTime(ec);
    runTime(ec.cloneMe());
  }

  private void runTime(EndCondition ec) throws Exception {
    ec.init(11, null, new OSMOConfiguration());
    long start = System.currentTimeMillis();
    int count = 0;
    while (!ec.endTest(null, null)) {
      Thread.sleep(100);
      count += 100;
      if (count > 2000) fail("End condition waiting for 1s should end before 2s. Now at "+count+"ms.");
    }
    long diff = System.currentTimeMillis() - start;
    assertTrue("Time taken with 1000 millisecond end condition should be 1000-2000, was " + diff, 1000 <= diff && diff <= 2000);
  }
}
