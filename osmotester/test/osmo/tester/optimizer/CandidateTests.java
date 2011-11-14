package osmo.tester.optimizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.MainGenerator;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.online.Candidate;
import osmo.tester.optimizer.online.SearchConfiguration;
import osmo.tester.optimizer.online.SearchingOptimizer;
import osmo.tester.testmodels.ValidTestModel2;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class CandidateTests {
  private MainGenerator generator;
  private SearchConfiguration sc;
  private SearchingOptimizer so;

  @Before
  public void init() {
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new ValidTestModel2(new Requirements()));
    generator = tester.initGenerator();
    sc = new SearchConfiguration(generator);
    so = new SearchingOptimizer(sc);

    generator = tester.initGenerator();
    generator.initSuite();
    so.setGenerator(generator);
  }

  @After
  public void teardown() {
    generator.endSuite();
  }

  @Test
  public void createOneSmallerThanMax() {
    sc.setPopulationSize(50);
    Candidate candidate = so.createCandidate();
    assertEquals("Number of tests in population", 50, candidate.size());
  }

  @Test
  public void createOneEqualToMax() {
    sc.setPopulationSize(1000);
    Candidate candidate = so.createCandidate();
    assertEquals("Number of tests in population", 1000, candidate.size());
  }

  @Test
  public void createTwoCandidates() {
    sc.setPopulationSize(50);
    Candidate candidate1 = so.createCandidate();
    Candidate candidate2 = so.createCandidate();
    assertEquals("Number of tests in population", 50, candidate1.size());
    assertEquals("Number of tests in population", 50, candidate2.size());
    assertFalse("Two generated test sets should not be identical", candidate1.equals(candidate2));
  }
}
