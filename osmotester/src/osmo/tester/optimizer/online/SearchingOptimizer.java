package osmo.tester.optimizer.online;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Teemu Kanstren */
public class SearchingOptimizer {
  private int iterations = 100;
  private int nOfCandidates = 1000;
  private Collection<TestCase> population = new ArrayList<TestCase>();

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public void search(OSMOTester tester) {
    Length maxLength = new Length(nOfCandidates);
    maxLength.setStrict(true);
    tester.addTestEndCondition(maxLength);
    tester.addSuiteEndCondition(maxLength);
    for (int i = 0 ; i < iterations ; i++) {
      sortTests(tester);
    }
  }

  private void sortTests(OSMOTester tester) {
    tester.generate();
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    Collection<TestCase> workList = new ArrayList<TestCase>();
    workList.addAll(population);
    workList.addAll(tests);
    Collection<Collection<TestCase>> candidates = new ArrayList<Collection<TestCase>>();
    for (int i = 0 ; i < nOfCandidates ; i++) {
      candidates.add(createCandidate(workList));
    }
  }

  private Collection<TestCase> createCandidate(Collection<TestCase> from) {
    Collection<TestCase> result = new ArrayList<TestCase>();
    
    return result;
  }

  private int calculateFitness(TestCase test) {
    Map<String, Object> status = new HashMap<String, Object>();
    for (TestCase tc : population) {
      //
    }
    return 0;
  }
}
