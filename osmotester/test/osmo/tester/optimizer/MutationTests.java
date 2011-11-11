package osmo.tester.optimizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.FitnessComparator;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchingOptimizer;
import osmo.tester.testmodels.ValidTestModel2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static osmo.common.TestUtils.cInt;

/** @author Teemu Kanstren */
public class MutationTests {
  private MainGenerator generator;
  private SearchingOptimizer so;

  @Before
  public void setup() {
    TestUtils.setRandom(new Random(111));
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new ValidTestModel2(new Requirements()));
    generator = tester.initGenerator();
    SearchConfiguration sc = new SearchConfiguration(generator);
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
    Candidate candidate = so.createCandidate();
    List<TestCase> tests = candidate.getTests();
    List<TestCase> original = new ArrayList<TestCase>();
    original.addAll(tests);
    so.mutate(candidate, 0.05);
    tests.removeAll(original);
    assertEquals("Number of mutated tests", 1, tests.size());
  }

  @Test
  public void mutateHalf() {
    Candidate candidate = so.createCandidate();
    List<TestCase> tests = candidate.getTests();
    List<TestCase> original = new ArrayList<TestCase>();
    original.addAll(tests);
    so.mutate(candidate, 0.50);
    tests.removeAll(original);
    assertEquals("Number of mutated tests", 25, tests.size());
  }

  @Test
  public void mutateAll() {
    Candidate candidate = so.createCandidate();
    List<TestCase> tests = candidate.getTests();
    List<TestCase> original = new ArrayList<TestCase>();
    original.addAll(tests);
    so.mutate(candidate, 1.00);
    tests.removeAll(original);
    assertEquals("Number of mutated tests",50, tests.size());
  }
}
