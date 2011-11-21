package osmo.tester.optimizer;

import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.online.SearchingOptimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.cInt;

/** @author Teemu Kanstren */
public class RecombinationTests {
  private SearchConfiguration sc = new SearchConfiguration(null);
  private SearchingOptimizer so;

  @Before
  public void setup() {
    TestUtils.setSeed(111);
    so = new SearchingOptimizer(sc);
  }

  @Test
  public void sorting() {
    List<Candidate> candidates = candidateList();
    FitnessComparator comparator = new FitnessComparator();
    Collections.sort(candidates, comparator);
    int previous = -1;
    Candidate best = null;
    for (Candidate candidate : candidates) {
      int current = candidate.getFitness();
      if (previous > 0) {
        assertTrue("Search generation should be sorted", previous <= current);
      }
      previous = current;
      best = candidate;
    }
    assertEquals("Best should be saved for the last", previous, best.getFitness());
  }

  @Test
  public void recombinationWithNoDuplicates() {
    Collection<Integer> sizes1 = new ArrayList<Integer>();
    Collection<Integer> sizes2 = new ArrayList<Integer>();
    for (int i = 0; i < 1000; i++) {
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
      sizes1.add(c3Tests.size());
      sizes2.add(c4Tests.size());
    }
    assertAverage(sizes1, 5);
    assertAverage(sizes2, 5);
  }

  private void assertAverage(Collection<Integer> from, int expected) {
    double total = 0;
    for (int i : from) {
      total += i;
    }
    int average = (int) Math.round(total / from.size());
    assertEquals("Average recombination size", expected, average);
  }

  @Test
  public void recombinationWithDuplicates() {
    Collection<Integer> sizes1 = new ArrayList<Integer>();
    Collection<Integer> sizes2 = new ArrayList<Integer>();
    for (int i = 0; i < 1000; i++) {
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
      sizes1.add(c3Tests.size());
      sizes2.add(c4Tests.size());
    }
    //in a set of 10 we have 4 same elements, so we should get on average 3 cell recombinations (10-4=6,6/2=3)
    assertAverage(sizes1, 3);
    assertAverage(sizes2, 3);
  }

  @Test
  public void recombinationWithDuplicatesInBeginning() {
    Collection<Integer> sizes1 = new ArrayList<Integer>();
    Collection<Integer> sizes2 = new ArrayList<Integer>();
    for (int i = 0; i < 1000; i++) {
      Candidate c1 = createCandidate(10);
      Candidate c2 = createCandidate(10);
      List<TestCase> c1Tests = c1.getTests();
      c1Tests.set(0, c2.get(4));
      c1Tests.set(1, c2.get(5));
      List<TestCase> c2Tests = c2.getTests();
      c2Tests.set(0, c1.get(2));
      c2Tests.set(1, c1.get(3));
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
      sizes1.add(c3Tests.size());
      sizes2.add(c4Tests.size());
    }
    //in a set of 10 we have 4 same elements, so we should get on average 3 cell recombinations (10-4=6,6/2=3)
    assertAverage(sizes1, 3);
    assertAverage(sizes2, 3);
  }

  @Test
  public void recombinationWithDuplicatesInEnd() {
    Collection<Integer> sizes1 = new ArrayList<Integer>();
    Collection<Integer> sizes2 = new ArrayList<Integer>();
    for (int i = 0; i < 1000; i++) {
      Candidate c1 = createCandidate(10);
      Candidate c2 = createCandidate(10);
      List<TestCase> c1Tests = c1.getTests();
      c1Tests.set(8, c2.get(0));
      c1Tests.set(9, c2.get(1));
      List<TestCase> c2Tests = c2.getTests();
      c2Tests.set(7, c1.get(4));
      c2Tests.set(6, c1.get(5));
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
      sizes1.add(c3Tests.size());
      sizes2.add(c4Tests.size());
    }
    //in a set of 10 we have 4 same elements, so we should get on average 3 cell recombinations (10-4=6,6/2=3)
    assertAverage(sizes1, 3);
    assertAverage(sizes2, 3);
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
    for (int i = 0; i < 10; i++) {
      candidates.add(createCandidate(cInt(2, 10)));
    }
    return candidates;
  }

  private Candidate createCandidate(int testCount) {
    List<TestCase> tests = new ArrayList<TestCase>();
    for (int i = 0; i < testCount; i++) {
      tests.add(testCase());
    }
    return new Candidate(sc, tests);
  }

  private TestCase testCase() {
    TestCase test = new TestCase();
    int transitions = cInt(2, 10);
    for (int i = 0; i < transitions; i++) {
      test.addStep(new FSMTransition("v" + i));
    }
    return test;
  }
}
