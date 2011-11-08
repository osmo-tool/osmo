package osmo.tester.optimizer;

import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchingOptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.cInt;

/** @author Teemu Kanstren */
public class PopulationTests {
  private SearchConfiguration sc = new SearchConfiguration();

  @Before
  public void setup() {
    TestUtils.setRandom(new Random(111));
  }

  @Test
  public void sorting() {
    SearchingOptimizer so = new SearchingOptimizer(sc);
    List<Candidate> toSort = candidateList();
    List<Candidate> sorted = so.nextGenerationFrom(toSort);
    int previous = -1;
    for (Candidate candidate : sorted) {
      int current = candidate.getFitness();
      if (previous > 0) {
        assertTrue("Search generation should be sorted", previous >= current);
      }
      previous = current;
    }
  }

  @Test
  public void recombinationWithNoDuplicates() {
    SearchingOptimizer so = new SearchingOptimizer(sc);
    Candidate c1 = createCandidate(10);
    Candidate c2 = createCandidate(10);
    Candidate[] recombination = so.recombine(c1, c2);
    assertEquals("Recombination should produce two offspring", 2, recombination.length);
    Candidate c3 = recombination[0];
    Candidate c4 = recombination[1];
    List<TestCase> c3Tests = c3.getTests();
    List<TestCase> c4Tests = c4.getTests();
    assertEquals("Recombined test should have parent size", 10, c3.size());
    assertEquals("Recombined test should have parent size", 10, c4.size());
    c3Tests.removeAll(c1.getTests());
    c4Tests.removeAll(c1.getTests());
    int c3Size = c3Tests.size();
    int c4Size = c4Tests.size();
    assertTrue("Recombination of 10 tests should have minimum of 2 tests from each, was " + c3Size, c3Size > 2);
    assertTrue("Recombination of 10 tests should have minimum of 2 tests from each, was " + c4Size, c4Size > 2);
  }

  @Test
  public void recombinationWithDuplicates() {
    SearchingOptimizer so = new SearchingOptimizer(sc);
    Candidate c1 = createCandidate(10);
    Candidate c2 = createCandidate(10);
    List<TestCase> c1Tests = c1.getTests();
    c1Tests.set(4, c2.get(0));
    c1Tests.set(7, c2.get(1));
    List<TestCase> c2Tests = c2.getTests();
    c2Tests.set(3, c1.get(0));
    c2Tests.set(7, c1.get(1));
    Candidate[] recombination = so.recombine(c1, c2);
    assertEquals("Recombination should produce two offspring", 2, recombination.length);
    Candidate c3 = recombination[0];
    Candidate c4 = recombination[1];
    List<TestCase> c3Tests = c3.getTests();
    List<TestCase> c4Tests = c4.getTests();
    assertEquals("Recombined test should have parent size", 10, c3.size());
    assertEquals("Recombined test should have parent size", 10, c4.size());
    assertNoDuplicatesIn(c3Tests);
    assertNoDuplicatesIn(c4Tests);
    c3Tests.removeAll(c1Tests);
    c4Tests.removeAll(c2Tests);
    int c3Size = c3Tests.size();
    int c4Size = c4Tests.size();
    assertTrue("Recombination of 10 tests with 2 duplicates should have minimum of 3 tests from each, was " + c3Size, c3Size > 2);
    assertTrue("Recombination of 10 tests with 2 duplicates should have minimum of 3 tests from each, was " + c4Size, c4Size > 2);
  }

  @Test
  public void testSomeMore() {
    fail("TBD some clarifying tests are needed to ensure all is fine");
  }

  private void assertNoDuplicatesIn(List<TestCase> tests) {
    for (TestCase toTest : tests) {
      int count = 0;
      for (TestCase test : tests) {
        if (test.equals(toTest)) {
          count++;
        }
      }
      assertEquals("Duplicate entries should not exist", 1, count);
    }
  }

  private List<Candidate> candidateList() {
    List<Candidate> candidates = new ArrayList<Candidate>();
    for (int i = 0 ; i < 10 ; i++) {
      candidates.add(createCandidate(cInt(2, 10)));
    }
    return candidates;
  }

  private Candidate createCandidate(int testCount) {
    List<TestCase> tests = new ArrayList<TestCase>();
    for (int i = 0 ; i < testCount ; i++) {
      tests.add(testCase());
    }
    return new Candidate(sc, tests);
  }

  private TestCase testCase() {
    TestCase test = new TestCase();
    int transitions = cInt(2, 10);
    for (int i = 0 ; i < transitions ; i++) {
      test.addStep(new FSMTransition("v" + i));
    }
    return test;
  }
}
