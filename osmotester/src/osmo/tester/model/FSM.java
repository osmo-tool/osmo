package osmo.tester.model;

import osmo.common.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the given test model parsed from the model objects provided by the user.
 * Produced by the parser and used by the generator to create actual test cases.
 * The test steps in here are represented as FSMTransitions.
 *
 * @author Teemu Kanstren
 */
public class FSM {
  private static final Logger log = new Logger(FSM.class);
  /** Key = transition name (from @TestStep("name")), Value = transition object */
  private Map<TransitionName, FSMTransition> transitions = new LinkedHashMap<>();
  /** List of specific guards, associated to groups or steps. */
  private List<FSMGuard> specificGuards = new ArrayList<>();
  /** List of generic guards that apply to all steps. */
  private Collection<InvocationTarget> genericGuards = new ArrayList<>();
  /** List of guards that should be associated to all but the step with the given name. */
  private List<FSMGuard> negatedGuards = new ArrayList<>();
  /** List of generic pre-methods that apply to all steps. */
  private Collection<InvocationTarget> genericPre = new ArrayList<>();
  /** List of generic post-methods that apply to all steps. */
  private Collection<InvocationTarget> genericPost = new ArrayList<>();
  /** List of specific pre-methods that apply to a specific step or group. */
  private List<FSMGuard> specificPre = new ArrayList<>();
  /** List of specific post-methods that apply to a specific step or group.. */
  private List<FSMGuard> specificPost = new ArrayList<>();
  /** List of methods to be executed before each test case. */
  private Collection<InvocationTarget> beforeTests = new ArrayList<>();
  /** List of methods to be executed after each test case. */
  private Collection<InvocationTarget> afterTests = new ArrayList<>();
  /** List of methods to be executed as last (final) steps of any test case. */
  private Collection<InvocationTarget> lastSteps = new ArrayList<>();
  /** List of methods to be executed before the overall test suite. */
  private Collection<InvocationTarget> beforeSuites = new ArrayList<>();
  /** List of methods to be executed after the overall test suite. */
  private Collection<InvocationTarget> afterSuites = new ArrayList<>();
  /** List of model end conditions defining when to stop test generation (single test, not suite). */
  private Collection<InvocationTarget> endConditions = new ArrayList<>();
  /** List of methods to invoke when there is an error in test generation/execution. */
  private Collection<InvocationTarget> onErrors = new ArrayList<>();
  /** List of methods to invoke when entering exploration mode */
  private Collection<InvocationTarget> explorationEnablers = new ArrayList<>();
  /** List of method to invoke when entering generation mode. */
  private Collection<InvocationTarget> generationEnablers = new ArrayList<>();
  /** List of model variables to store for each test step. */
  private Collection<VariableField> modelVariables = new ArrayList<>();
  /** User defined requirements. */
  private Requirements requirements = null;
  /** The set of objects to call to get current user defined state. Key = model object name, value=target method to get coverage value.*/
  private Collection<CoverageMethod> coverageValues = new ArrayList<>();
  /** Name of the start step (before anything else). */
  public static final String START_STEP_NAME = ".osmo.tester.start.step";
//  private static boolean checked = false;

  /** Constructor. And a useful comment. */
  public FSM() {
  }

  /**
   * Returns an existing object for the requested test step name or creates a new one if one was not previously
   * found existing.
   *
   * @param name   The name of the test step. Taken from @TestStep("name").
   * @param weight The weight of the test step. Taken from @TestStep(weight=x).
   * @return A transition object for the requested name.
   */
  public FSMTransition createTransition(TransitionName name, int weight) {
    log.d("Creating transition: " + name + " weight:" + weight);
    FSMTransition transition = transitions.get(name);
    if (transition != null) {
      //we can come here from guard, post, or transition creation. however, only transitions define weights
      //so we have to set it here if it was previously not defined
      //note that transition.setWeight will do nothing in case of negative value as in guards and oracles
      transition.setWeight(weight);
      return transition;
    }
    transition = new FSMTransition(name);
    transition.setWeight(weight);
    transitions.put(name, transition);
    log.d("Transition created");
    return transition;
  }

  /**
   * Checks this model for validity.
   * Note that most checks for specific annotations are done already in the associated
   * {@link osmo.tester.parser.AnnotationParser} object.
   *
   * @param errors Errors to report in addition to those found here.
   */
  public void checkFSM(StringBuilder errors) {
//    if (checked) return;
//    checked = true;
    log.d("Checking FSM validity");
    if (transitions.size() == 0) {
      errors.append("No test steps found in given model object. Model cannot be processed.\n");
    }
    List<String> transitionNames = new ArrayList<>();
    List<String> groupNames = new ArrayList<>();
    for (FSMTransition transition : transitions.values()) {
      InvocationTarget target = transition.getTransition();
      TransitionName name = transition.getName();
      log.d("Checking test step:" + name);
      if (target == null) {
        errors.append("Test step without invocation target" + name + "\n");
        log.d("Error: Found transition without invocation target - " + name);
      }
      addGenericElements(transition, errors);
      addSpecificGuards(transition, errors);
      addSpecificPrePosts(transition, errors);
      addNegatedGuards(transition, errors);
      transition.sort();
      transitionNames.add(transition.getStringName());
      String groupName = transition.getGroupName().toString();
      if (groupName.length() > 0) {
        groupNames.add(groupName);
      }
    }
    checkGuards(specificGuards, errors, "@Guard");
    checkGuards(negatedGuards, errors, "Negation");
    checkGuards(specificPre, errors, "@Pre");
    checkGuards(specificPost, errors, "@Post");
    for (String groupName : groupNames) {
      if (transitionNames.contains(groupName)) {
        errors.append("Group name same as a step name ("+groupName+"). Must be different.\n");
      }
    }
    if (errors.length() > 0) {
      throw new IllegalStateException("Invalid test model:\n" + errors);
    }
    log.d("FSM checked");
  }

  private void checkGuards(List<FSMGuard> guards, StringBuilder errors, String errorMsg) {
    for (FSMGuard guard : guards) {
      if (guard.getCount() == 0) {
        TransitionName name = guard.getName();
        //length 0 is the case where user has used what is considered camelcase notation but has not given valid name
        //since that should have been caught already before, we do not add another e for it here
        //TODO: tests for these
        if (name.toString().length() == 0) return;
        errors.append(errorMsg+" without matching step:" + name +".\n");
      }
    }
  }

  /**
   * Add generic guards and pre- post- methods to all test steps.
   *
   * @param step       The test step to check.
   * @param errors     The current error message string.
   */
  private void addGenericElements(FSMTransition step, StringBuilder errors) {
    //we add all generic guards to the set of guards for this step. doing it here includes them in the checks
    for (InvocationTarget guard : genericGuards) {
      step.addGuard(guard);
    }
    for (InvocationTarget pre : genericPre) {
      step.addPre(pre);
    }
    for (InvocationTarget post : genericPost) {
      step.addPost(post);
    }
  }

  /**
   * Adds guards annotated for specific transitions and groups.
   *
   * @param transition The transition to process.
   * @param errors Possible errors so far.
   */
  private void addSpecificGuards(FSMTransition transition, StringBuilder errors) {
    TransitionName name = transition.getName();
    TransitionName groupName = transition.getGroupName();
    for (FSMGuard guard : specificGuards) {
      TransitionName guardName = guard.getName();
      if (name.equals(guardName) || groupName.equals(guardName)) {
        log.d("Adding guard " + guardName + " to transition " + name);
        transition.addGuard(guard.getTarget());
        guard.found();
      }
    }
  }

  /**
   * Adds annotated pre- and post-methods for specific test steps and groups.
   *
   * @param transition The transition to process.
   * @param errors Possible errors so far.
   */
  private void addSpecificPrePosts(FSMTransition transition, StringBuilder errors) {
    TransitionName name = transition.getName();
    TransitionName groupName = transition.getGroupName();
    for (FSMGuard pre : specificPre) {
      TransitionName preName = pre.getName();
      if (name.equals(preName) || groupName.equals(preName)) {
        log.d("Adding pre " + preName + " to transition " + name);
        transition.addPre(pre.getTarget());
        pre.found();
      }
    }
    for (FSMGuard post : specificPost) {
      TransitionName postName = post.getName();
      if (name.equals(postName) || groupName.equals(postName)) {
        log.d("Adding post " + postName + " to transition " + name);
        transition.addPost(post.getTarget());
        post.found();
      }
    }
  }

  /**
   * Adds negated guards (the !name naming) annotated to matching transitions.
   *
   * @param transition The transition to process.
   * @param errors Possible errors so far.
   * @return The old and new errors.
   */
  private void addNegatedGuards(FSMTransition transition, StringBuilder errors) {
    TransitionName name = transition.getName();
    TransitionName groupName = transition.getGroupName();
    for (FSMGuard guard : negatedGuards) {
      TransitionName guardName = guard.getName();
      if (name.shouldNegationApply(guardName) || groupName.shouldNegationApply(guardName)) {
        log.d("Adding negated guard " + guardName + " to transition " + name);
        transition.addGuard(guard.getTarget());
        guard.found();
      }
    }
  }

  public FSMTransition getTransition(TransitionName name) {
    return transitions.get(name);
  }

  public FSMTransition getTransition(String name) {
    for (TransitionName tName : transitions.keySet()) {
      if (tName.toString().equals(name)) {
        return transitions.get(tName);
      }
    }
    return null;
  }

  public Collection<FSMTransition> getTransitions() {
    return transitions.values();
  }

  public void addAfter(InvocationTarget target) {
    afterTests.add(target);
  }

  public void addBefore(InvocationTarget target) {
    beforeTests.add(target);
  }

  public void addAfterSuite(InvocationTarget target) {
    afterSuites.add(target);
  }

  public void addBeforeSuite(InvocationTarget target) {
    beforeSuites.add(target);
  }

  public Collection<InvocationTarget> getBeforeTests() {
    return beforeTests;
  }

  public Collection<InvocationTarget> getAfterTests() {
    return afterTests;
  }

  public Collection<InvocationTarget> getBeforeSuites() {
    return beforeSuites;
  }

  public Collection<InvocationTarget> getAfterSuites() {
    return afterSuites;
  }

  public Collection<InvocationTarget> getEndConditions() {
    return endConditions;
  }

  /**
   * Add a guard that should return true for all test steps in the test model.
   *
   * @param target The guard method to be invoked for evaluation.
   */
  public void addGenericGuard(InvocationTarget target) {
    genericGuards.add(target);
  }

  /**
   * Add a pre-method that should be executed for a specific test step or group in the test model.
   *
   * @param name Name of step or group to associate with.
   * @param target The pre method to be invoked for evaluation.
   */
  public void addSpecificPre(TransitionName name, InvocationTarget target) {
    specificPre.add(new FSMGuard(name, target));
  }

  /**
   * Add a pre-method that should be executed for all test steps in the test model.
   *
   * @param target The pre method to be invoked for evaluation.
   */
  public void addGenericPre(InvocationTarget target) {
    genericPre.add(target);
  }

  /**
   * Add a post-method that should be executed for all test step in the test model.
   *
   * @param target The post method to be invoked for evaluation.
   */
  public void addGenericPost(InvocationTarget target) {
    genericPost.add(target);
  }

  /**
   * Add a post-method that should be executed for a specific test step or group in the test model.
   *
   * @param name Name of step or group to associate with.
   * @param target The post method to be invoked for evaluation.
   */
  public void addSpecificPost(TransitionName name, InvocationTarget target) {
    specificPost.add(new FSMGuard(name, target));
  }

  /**
   * Add an end condition that should be checked for test case generation termination condition.
   *
   * @param target The end condition.
   */
  public void addEndCondition(InvocationTarget target) {
    endConditions.add(target);
  }

  /**
   * Add a state variable field for which to store value in each test step.
   *
   * @param var The variable field itself.
   */
  public void addModelVariable(VariableField var) {
    modelVariables.add(var);
  }

  public void addLastStep(InvocationTarget lastStep) {
    lastSteps.add(lastStep);
  }

  public Collection<InvocationTarget> getLastSteps() {
    return lastSteps;
  }

  /**
   * State variables as tagged by @Variable annotations.
   *
   * @return variables tagged @Variable.
   */
  public Collection<VariableField> getModelVariables() {
    return modelVariables;
  }

  public Requirements getRequirements() {
    return requirements;
  }

  public void setRequirements(Requirements requirements) {
    this.requirements = requirements;
  }

  public void addSpecificGuard(TransitionName name, InvocationTarget target) {
    FSMGuard sg = new FSMGuard(name, target);
    specificGuards.add(sg);
  }

  public void addNegatedGuard(TransitionName name, InvocationTarget target) {
    FSMGuard ng = new FSMGuard(name, target);
    negatedGuards.add(ng);
  }

  public void addExplorationEnabler(InvocationTarget target) {
    explorationEnablers.add(target);
  }

  public void addGenerationEnabler(InvocationTarget target) {
    generationEnablers.add(target);
  }

  public Collection<InvocationTarget> getExplorationEnablers() {
    return explorationEnablers;
  }

  public Collection<InvocationTarget> getGenerationEnablers() {
    return generationEnablers;
  }

  public void addCoverageMethod(CoverageMethod coverageMethod) {
    this.coverageValues.add(coverageMethod);
  }

  public Collection<CoverageMethod> getCoverageMethods() {
    return coverageValues;
  }

  public void addOnError(InvocationTarget target) {
    onErrors.add(target);
  }

  public Collection<InvocationTarget> getOnErrors() {
    return onErrors;
  }
}
