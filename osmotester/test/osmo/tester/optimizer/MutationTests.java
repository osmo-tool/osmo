package osmo.tester.optimizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchingOptimizer;
import osmo.tester.testmodels.ValidTestModel2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class MutationTests {
  private MainGenerator generator;
  private SearchingOptimizer so;

  @Before
  public void setup() {
    TestUtils.setSeed(111);
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new ValidTestModel2(new Requirements()));
    generator = tester.initGenerator();
    SearchConfiguration sc = new SearchConfiguration(generator);
    sc.setSeed(111);
    so = new SearchingOptimizer(sc);

    generator.initSuite();
    so.setGenerator(generator);
  }

  @After
  public void teardown() {
    generator.endSuite();
  }

  @Test
  public void mutateOne() {
    assertMutations(0.02, 1);
  }

  private void assertMutations(double mutationProbability, int expected) {
    Candidate candidate = so.createCandidate();
    Collection<Integer> mutations = new ArrayList<Integer>();
    for (int i = 0; i < 100; i++) {
      List<TestCase> before = new ArrayList<TestCase>();
      before.addAll(candidate.getTests());
      so.mutate(candidate, mutationProbability);
      List<TestCase> after = new ArrayList<TestCase>();
      after.addAll(candidate.getTests());
      after.removeAll(before);
      mutations.add(after.size());
    }
    assertEquals("Number of test runs", 100, mutations.size());
    int average = 0;
    for (Integer mutation : mutations) {
      average += mutation;
    }
    average /= 100;
    assertEquals("Average number of mutated tests", expected, average);
  }

  @Test
  public void mutateHalf() {
    assertMutations(0.50, 25);
  }

  @Test
  public void mutateAll() {
    Candidate candidate = so.createCandidate();
    List<TestCase> tests = candidate.getTests();
    List<TestCase> original = new ArrayList<TestCase>();
    original.addAll(tests);
    so.mutate(candidate, 1.00);
    tests.removeAll(original);
    assertEquals("Number of mutated tests", 50, tests.size());
  }
}
