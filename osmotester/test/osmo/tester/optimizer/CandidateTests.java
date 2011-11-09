package osmo.tester.optimizer;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchingOptimizer;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class CandidateTests {
  private Collection<TestCase> tests = createTestSet(1000);

  private Collection<TestCase> createTestSet(int size) {
    Collection<TestCase> tests = new ArrayList<TestCase>();
    for (int i = 0; i < size; i++) {
      tests.add(new TestCase());
    }
    return tests;
  }

  @Test
  public void createOneSmallerThanMax() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setPopulationSize(50);
    SearchingOptimizer so = new SearchingOptimizer(sc);
    Candidate candidate = so.createCandidate(tests);
    assertEquals("Number of tests in population", 50, candidate.size());
  }

  @Test
  public void createOneEqualToMax() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setPopulationSize(1000);
    SearchingOptimizer so = new SearchingOptimizer(sc);
    Candidate candidate = so.createCandidate(tests);
    assertEquals("Number of tests in population", 1000, candidate.size());
  }

  @Test
  public void createBiggerThanMax() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setPopulationSize(1001);
    SearchingOptimizer so = new SearchingOptimizer(sc);
    try {
      Candidate candidate = so.createCandidate(tests);
      fail("Requesting population bigger than source set size should fail");
    } catch (IllegalArgumentException e) {
      assertEquals("Exception message", "Requested population of 1001 from set of 1000 tests. Population size cannot be bigger than source set size.", e.getMessage());
    }
  }

  @Test
  public void createTwoCandidates() {
    SearchConfiguration sc = new SearchConfiguration(null);
    sc.setPopulationSize(50);
    SearchingOptimizer so = new SearchingOptimizer(sc);
    Candidate candidate1 = so.createCandidate(tests);
    Candidate candidate2 = so.createCandidate(tests);
    assertEquals("Number of tests in population", 50, candidate1.size());
    assertEquals("Number of tests in population", 50, candidate2.size());
    assertFalse("Two generated test sets should not be identical", candidate1.equals(candidate2));
  }
}
