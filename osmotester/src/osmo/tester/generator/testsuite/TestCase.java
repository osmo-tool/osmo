package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class describes a single test case and all test steps that it contains.
 * This also includes a list of added coverage for model transitions and requirements.
 * Note that this coverage information may or may not hold in current testing.
 * Note that this information needs to be updated if optimization to test suite ordering etc. is applied
 * after the suite has been generated.
 * All optimizers provided with OSMOTester will also update the added coverage information of the tests.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class TestCase {
  /** The test steps (taken) for this test case. */
  private List<TestStep> steps = new ArrayList<TestStep>();
  /** The latest test step (being/having been generated). */
  private TestStep currentStep = null;
  /** Newly covered transitions in relation to generation history. See class header for notes.
  *  NOTE: we use a Set to avoid duplicates if the same transition is covered multiple times. */
  private Collection<FSMTransition> addedTransitionCoverage = new HashSet<FSMTransition>();
  /** Newly covered requirements in relation to generation history. See class header for notes.
   *  NOTE: we use a Set to avoid duplicates if the same requirement is covered multiple times. */
  private Collection<String> addedRequirementsCoverage = new HashSet<String>();
  /** Unique identifier for this test case. */
  private final int id;
  /** The next identifier in line to set for test cases. */
  private static AtomicInteger nextId = new AtomicInteger(1);
  /** Identifier for next test case step. */
  private int nextStepId = 1;

  public TestCase() {
    this.id = nextId.getAndIncrement();
  }

  /**
   * @return Unique id for this test case.
   */
  public int getId() {
    return id;
  }

  public TestStep getCurrentStep(){
    return currentStep;
  }

  /**
   * Adds a new test step.
   *
   * @param transition The transition for the test step. 
   */
  public TestStep addStep(FSMTransition transition) {
    TestStep step = new TestStep(transition, nextStepId++);
    steps.add(step);
    currentStep = step;
    return step;
  }

  /**
   * Defines that the current test step covered the given requirement.
   *
   * @param requirement The covered requirement identifier.
   */
  public void covered(String requirement) {
    currentStep.covered(requirement);
  }

  /**
   * Get list of test steps generated (so far) for this test case.
   *
   * @return List of test steps (transitions).
   */
  public List<TestStep> getSteps() {
    return steps;
  }

  /**
   * Clear list of added transitions and requirements coverage.
   * Useful in test suite optimization when these lists need to be updated.
   */
  public void resetCoverage() {
    addedRequirementsCoverage.clear();
    addedTransitionCoverage.clear();
  }

  public Collection<FSMTransition> getAddedTransitionCoverage() {
    return addedTransitionCoverage;
  }

  public Collection<FSMTransition> getCoveredTransitions(){
      Collection<FSMTransition> transitionCoverage = new HashSet<FSMTransition>();
    for(TestStep teststep: steps){
    	transitionCoverage.add(teststep.getTransition());
    }
    return transitionCoverage;
  }
  
  public Collection<String> getCoveredRequirements(){
	  Collection<String> requirementsCoverage = new HashSet<String>();
    for(TestStep teststep: steps){
      requirementsCoverage.addAll(teststep.getCoveredRequirements());
    }
    return requirementsCoverage;
  }
  
  public void addAddedTransitionCoverage(FSMTransition transition) {
    addedTransitionCoverage.add(transition);
  }

  public Collection<String> getAddedRequirementsCoverage() {
    return addedRequirementsCoverage;
  }

  public void addAddedRequirementsCoverage(String requirement) {
    addedRequirementsCoverage.add(requirement);
  }

  @Override
  public String toString() {
    return "TestCase{" +
            "steps=" + steps +
            '}';
  }
}
