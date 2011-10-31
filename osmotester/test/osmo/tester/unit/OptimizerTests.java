package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.RequirementsOptimizer;
import osmo.tester.optimizer.TransitionOptimizer;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class OptimizerTests {
  @Test
  public void requirementsOptimizer3TestsNoOverlap() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.covered("r1");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.covered("r3");
    suite.covered("r4");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t5"));
    suite.addStep(new FSMTransition("t6"));
    suite.addStep(new FSMTransition("t7"));
    suite.endTest();

    RequirementsOptimizer optimizer = new RequirementsOptimizer();
    List<TestCase> tests = optimizer.optimize(suite);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> added1 = testCase1.getAddedRequirementsCoverage();
    Collection<String> added2 = testCase2.getAddedRequirementsCoverage();
    Collection<String> added3 = testCase3.getAddedRequirementsCoverage();
    assertEquals("Number of new requirements covered by test 1.", 3, added1.size());
    assertEquals("Number of new requirements covered by test 2.", 1, added2.size());
    assertEquals("Number of new requirements covered by test 3.", 0, added3.size());
  }

  @Test
  public void requirementsOptimizer3TestsWithOverlap() {
    RequirementsOptimizer optimizer = new RequirementsOptimizer();
    TestSuite suite = createSuite1();
    List<TestCase> tests = optimizer.optimize(suite);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> added1 = testCase1.getAddedRequirementsCoverage();
    Collection<String> added2 = testCase2.getAddedRequirementsCoverage();
    Collection<String> added3 = testCase3.getAddedRequirementsCoverage();
    assertEquals("Number of new requirements covered by test 1.", 2, added1.size());
    assertEquals("Number of new requirements covered by test 2.", 0, added2.size());
    assertEquals("Number of new requirements covered by test 3.", 0, added3.size());
  }

  @Test
  public void requirementsOptimizer3TestsWithCunningOverlap() {
    RequirementsOptimizer optimizer = new RequirementsOptimizer();
    TestSuite suite = createSuite2();
    List<TestCase> tests = optimizer.optimize(suite);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> added1 = testCase1.getAddedRequirementsCoverage();
    Collection<String> added2 = testCase2.getAddedRequirementsCoverage();
    Collection<String> added3 = testCase3.getAddedRequirementsCoverage();
    System.out.println("added1:" + added1);
    System.out.println("added2:" + added2);
    System.out.println("added3:" + added3);
    assertEquals("Number of new requirements covered by test 1.", 3, added1.size());
    assertEquals("Number of new requirements covered by test 2.", 2, added2.size());
    assertEquals("Number of new requirements covered by test 3.", 1, added3.size());
  }

  private TestSuite createSuite1() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.covered("r1");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.covered("r1");
    suite.covered("r2");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t5"));
    suite.addStep(new FSMTransition("t6"));
    suite.addStep(new FSMTransition("t7"));
    suite.addStep(new FSMTransition("t8"));
    suite.endTest();
    return suite;
  }

  private TestSuite createSuite2() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.covered("r5");
    suite.covered("r1");
    suite.endTest();

    suite.startTest();
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.endTest();

    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.covered("r3");
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r4");
    suite.covered("r6");
    suite.addStep(new FSMTransition("t4"));
    suite.endTest();
    return suite;
  }

  @Test
  public void transitionOptimizer3TestsNoOverlap() {
    TestSuite suite = createSuite1();
    TransitionOptimizer optimizer = new TransitionOptimizer();
    List<TestCase> tests = optimizer.optimize(suite);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<FSMTransition> added1 = testCase1.getAddedTransitionCoverage();
    Collection<FSMTransition> added2 = testCase2.getAddedTransitionCoverage();
    Collection<FSMTransition> added3 = testCase3.getAddedTransitionCoverage();
    assertEquals("Number of new transitions covered by test 1.", 4, added1.size());
    assertEquals("Number of new transitions covered by test 2.", 3, added2.size());
    assertEquals("Number of new transitions covered by test 3.", 1, added3.size());
  }

  @Test
  public void transitionOptimizer3TestsWithOverlap() {
    TestSuite suite = createSuite2();
    TransitionOptimizer optimizer = new TransitionOptimizer();
    List<TestCase> tests = optimizer.optimize(suite);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<FSMTransition> added1 = testCase1.getAddedTransitionCoverage();
    Collection<FSMTransition> added2 = testCase2.getAddedTransitionCoverage();
    Collection<FSMTransition> added3 = testCase3.getAddedTransitionCoverage();
    assertEquals("Number of new transitions covered by test 1.", 4, added1.size());
    assertEquals("Number of new transitions covered by test 2.", 0, added2.size());
    assertEquals("Number of new transitions covered by test 3.", 0, added3.size());
  }

  @Test
  public void ratOptimizer() {
    //TODO: needs to be implemented
  }
}
