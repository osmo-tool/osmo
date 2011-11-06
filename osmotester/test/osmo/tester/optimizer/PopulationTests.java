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
import java.util.Collection;
import java.util.List;
import java.util.Random;

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
  }

  private List<Candidate> candidateList() {
    List<Candidate> candidates = new ArrayList<Candidate>();
    for (int i = 0 ; i < 10 ; i++) {
      candidates.add(candidate());
    }
    return candidates;
  }

  private Candidate candidate() {
    Collection<TestCase> tests = new ArrayList<TestCase>();
    int count = cInt(2, 10);
    for (int i = 0 ; i < count ; i++) {
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
