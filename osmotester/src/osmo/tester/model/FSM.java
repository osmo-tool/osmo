package osmo.tester.model;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.model.data.ValueSet;
import osmo.tester.parser.field.SearchableInputField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the given model object in terms of a finite state machine (FSM).
 * Produced by the parser and used by the generator to create actual test cases.
 *
 * @author Teemu Kanstren
 */
public class FSM {
  private static final Logger log = new Logger(FSM.class);
  /** Key = transition name (from @Transition("name")), Value = transition object */
  private Map<TransitionName, FSMTransition> transitions = new HashMap<>();
  /** List of steps to execute after test is otherwise done. */
  private List<InvocationTarget> lastSteps = new ArrayList<>();
  /** List of specific guards, associated to groups or transitions. */
  private List<FSMGuard> specificGuards = new ArrayList<>();
  /** List of generic guards that apply to all transitions. */
  private Collection<InvocationTarget> genericGuards = new ArrayList<>();
  /** List of guards that should be associated to all but the given name. */
  private List<FSMGuard> negatedGuards = new ArrayList<>();
  /** List of generic pre-methods that apply to all transitions. */
  private Collection<InvocationTarget> genericPre = new ArrayList<>();
  /** List of generic post-methods that apply to all transitions. */
  private Collection<InvocationTarget> genericPost = new ArrayList<>();
  /** List of specific pre-methods that apply to a specific transition or group. */
  private List<FSMGuard> specificPre = new ArrayList<>();
  /** List of specific post-methods that apply to a specific transition or group.. */
  private List<FSMGuard> specificPost = new ArrayList<>();
  /** List of methods to be executed before each test case. */
  private Collection<InvocationTarget> befores = new ArrayList<>();
  /** List of methods to be executed after each test case. */
  private Collection<InvocationTarget> afters = new ArrayList<>();
  /** List of methods to be executed before the overall test suite. */
  private Collection<InvocationTarget> beforeSuites = new ArrayList<>();
  /** List of methods to be executed after the overall test suite. */
  private Collection<InvocationTarget> afterSuites = new ArrayList<>();
  /** List of conditions when to stop (prematurely) test generation (single test, not suite). */
  private Collection<InvocationTarget> endConditions = new ArrayList<>();
  //TODO: remove one
  private Collection<InvocationTarget> explorationEnablers = new ArrayList<>();
  private Collection<InvocationTarget> generationEnablers = new ArrayList<>();
  /** List of state variables to store for each test step. */
  private Collection<VariableField> stateVariables = new ArrayList<>();
  /** List of variables that can be used for coverage calculations. */
  private Collection<VariableField> coverageVariables = new ArrayList<>();
  /** The list of {@link osmo.tester.model.data.SearchableInput} elements parsed from model objects. */
  private Collection<SearchableInput> searchableInputs = new ArrayList<>();
  /** We read the {@link osmo.tester.model.data.SearchableInput} values from these when tests start. */
  private Collection<SearchableInputField> searchableInputFields = new ArrayList<>();
  /** User defined requirements. */
  private Requirements requirements = null;
  private Map<String, InvocationTarget> stateNames = new HashMap<>();
  public static final String START_NAME = ".osmo.tester.init";

  /** Constructor. */
  public FSM() {
  }

  /**
   * Returns an existing object for the requested transition name or creates a new one if one was not previously
   * found existing.
   *
   * @param name   The name of the transition. Taken from @Transition("name").
   * @param weight The weight of the transition. Taken from @Transition(weight=x).
   * @return A transition object for the requested name.
   */
  public FSMTransition createTransition(TransitionName name, int weight) {
    log.debug("Creating transition: " + name + " weight:" + weight);
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
    log.debug("Transition created");
    return transition;
  }

  /**
   * Checks the FSM for validity according to generic elements. This includes the following constraints:
   * -Is a requirements object defined in the model? if so use it, otherwise create empty one.
   * -Check that each @Guard, @Pre and @Post  has a matching transition.
   * Note that most checks for specific annotations are done already in the associated
   * {@link osmo.tester.parser.AnnotationParser} object.
   *
   * @param errors Previously defined errors to be reported in addition to new ones found.
   */
  public void checkFSM(String errors) {
    log.debug("Checking FSM validity");
    if (transitions.size() == 0) {
      errors += "No transitions found in given model object. Model cannot be processed.\n";
    }
    List<String> transitionNames = new ArrayList<>();
    List<String> groupNames = new ArrayList<>();
    for (FSMTransition transition : transitions.values()) {
      InvocationTarget target = transition.getTransition();
      TransitionName name = transition.getName();
      log.debug("Checking transition:" + name);
      if (target == null) {
        errors += "Transition without invocation target" + name + "\n";
        log.debug("Error: Found transition without invocation target - " + name);
      }
      errors = addGenericElements(transition, errors);
      errors = addSpecificGuards(transition, errors);
      errors = addSpecificPrePosts(transition, errors);
      errors = addNegatedGuards(transition, errors);
      transition.sort();
      transitionNames.add(transition.getStringName());
      String groupName = transition.getGroupName().toString();
      if (groupName.length() > 0) {
        groupNames.add(groupName);
      }
    }
    errors = checkGuards(specificGuards, errors, "Guard");
    errors = checkGuards(negatedGuards, errors, "Negation");
    errors = checkGuards(specificPre, errors, "Pre");
    errors = checkGuards(specificPost, errors, "Post");
    for (String groupName : groupNames) {
      if (transitionNames.contains(groupName)) {
        errors += "Groupname same as a step name ("+groupName+"). Must be different.\n";
      }
    }
    if (errors.length() > 0) {
      throw new IllegalStateException("Invalid FSM:\n" + errors);
    }
    Collections.sort(lastSteps);
    log.debug("FSM checked");
  }
  
  private String checkGuards(List<FSMGuard> guards, String errors, String errorMsg) {
    for (FSMGuard guard : guards) {
      if (guard.getCount() == 0) {
        errors += errorMsg+" without matching transition:" + guard.getName()+".\n";
      }
    }
    return errors;
  }

  /**
   * Add generic guards and oracles to all transitions.
   *
   * @param transition The transition to check.
   * @param errors     The current error message string.
   * @return The error msg string given with possible new errors appended.
   */
  private String addGenericElements(FSMTransition transition, String errors) {
    //we add all generic guards to the set of guards for this transition. doing it here includes them in the checks
    for (InvocationTarget guard : genericGuards) {
      transition.addGuard(guard);
    }
    for (InvocationTarget pre : genericPre) {
      transition.addPre(pre);
    }
    for (InvocationTarget post : genericPost) {
      transition.addPost(post);
    }
    return errors;
  }

  /**
   * Adds guards annotated for specific transitions and groups.
   * 
   * @param transition The transition to process.
   * @param errors Possible errors so far.
   * @return The old and new errors.
   */
  private String addSpecificGuards(FSMTransition transition, String errors) {
    TransitionName name = transition.getName();
    TransitionName groupName = transition.getGroupName();
    for (FSMGuard guard : specificGuards) {
      TransitionName guardName = guard.getName();
      if (name.equals(guardName) || groupName.equals(guardName)) {
        log.debug("Adding guard "+guardName+" to transition "+name);
        transition.addGuard(guard.getTarget());
        guard.found();
      }
    }
    return errors;
  }

  /**
   * Adds annotated pre- and post-methods for specific transitions and groups.
   *
   * @param transition The transition to process.
   * @param errors Possible errors so far.
   * @return The old and new errors.
   */
  private String addSpecificPrePosts(FSMTransition transition, String errors) {
    TransitionName name = transition.getName();
    TransitionName groupName = transition.getGroupName();
    for (FSMGuard pre : specificPre) {
      TransitionName preName = pre.getName();
      if (name.equals(preName) || groupName.equals(preName)) {
        log.debug("Adding pre "+preName+" to transition "+name);
        transition.addPre(pre.getTarget());
        pre.found();
      }
    }
    for (FSMGuard post : specificPost) {
      TransitionName postName = post.getName();
      if (name.equals(postName) || groupName.equals(postName)) {
        log.debug("Adding post "+postName+" to transition "+name);
        transition.addPost(post.getTarget());
        post.found();
      }
    }
    return errors;
  }

  /**
   * Adds negated guards (the !name naming) annotated to matching transitions.
   *
   * @param transition The transition to process.
   * @param errors Possible errors so far.
   * @return The old and new errors.
   */
  private String addNegatedGuards(FSMTransition transition, String errors) {
    TransitionName name = transition.getName();
    TransitionName groupName = transition.getGroupName();
    for (FSMGuard guard : negatedGuards) {
      TransitionName guardName = guard.getName();
      if (name.shouldNegationApply(guardName) || groupName.shouldNegationApply(guardName)) {
        log.debug("Adding negated guard "+guardName+" to transition "+name);
        transition.addGuard(guard.getTarget());
        guard.found();
      }
    }
    return errors;
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
    afters.add(target);
  }

  public void addBefore(InvocationTarget target) {
    befores.add(target);
  }

  public void addAfterSuite(InvocationTarget target) {
    afterSuites.add(target);
  }

  public void addBeforeSuite(InvocationTarget target) {
    beforeSuites.add(target);
  }

  public void addLastStep(InvocationTarget lastStep) {
    lastSteps.add(lastStep);
  }

  public Collection<InvocationTarget> getBefores() {
    return befores;
  }

  public Collection<InvocationTarget> getAfters() {
    return afters;
  }

  public Collection<InvocationTarget> getBeforeSuites() {
    return beforeSuites;
  }

  public Collection<InvocationTarget> getAfterSuites() {
    return afterSuites;
  }

  public Collection<InvocationTarget> getLastSteps() {
    return lastSteps;
  }

  public Collection<InvocationTarget> getEndConditions() {
    return endConditions;
  }

  /**
   * Add a guard that should return true for all transitions in the test model.
   *
   * @param target The guard method to be invoked for evaluation.
   */
  public void addGenericGuard(InvocationTarget target) {
    genericGuards.add(target);
  }

  /**
   * Add a pre-method that should be executed for a specific transitions or group in the test model.
   *
   * @param target The pre method to be invoked for evaluation.
   */
  public void addSpecificPre(TransitionName name, InvocationTarget target) {
    specificPre.add(new FSMGuard(name, target));
  }

  /**
   * Add a pre-method that should be executed for all transitions in the test model.
   *
   * @param target The pre method to be invoked for evaluation.
   */
  public void addGenericPre(InvocationTarget target) {
    genericPre.add(target);
  }

  /**
   * Add a post-method that should be executed for all transitions in the test model.
   *
   * @param target The post method to be invoked for evaluation.
   */
  public void addGenericPost(InvocationTarget target) {
    genericPost.add(target);
  }

  /**
   * Add a post-method that should be executed for a specific transitions or group in the test model.
   *
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
  public void addStateVariable(VariableField var) {
    stateVariables.add(var);
  }

  public void addCoverageVariable(VariableField var) {
    coverageVariables.add(var);
  }

  /**
   * State variables as tagged by @Variable annotations. Does not include {@link osmo.tester.model.data.SearchableInput} classes.
   *
   * @return variables tagged @Variable.
   */
  public Collection<VariableField> getStateVariables() {
    return stateVariables;
  }

  public Collection<VariableField> getCoverageVariables() {
    return coverageVariables;
  }

  /**
   * These provide access to the {@link osmo.tester.model.data.SearchableInput} elements, to enable capturing changes between tests.
   *
   * @return The access codes for the searchable inputs.
   */
  public Collection<SearchableInputField> getSearchableInputFields() {
    return searchableInputFields;
  }

  /**
   * Model variables extending the {@link osmo.tester.model.data.SearchableInput} class. Does not include @Variable annotated classes.
   *
   * @return Variables extending {@link osmo.tester.model.data.SearchableInput} classes.
   */
  public Collection<SearchableInput> getSearchableInputs() {
    return searchableInputs;
  }

  /** Resets the stored fields for new test. */
  public void clearSearchableInputs() {
    searchableInputs.clear();
  }

  /**
   * Add a new access method for a {@link osmo.tester.model.data.SearchableInput} to the model.
   *
   * @param field to add.
   */
  public void addSearchableInputField(SearchableInputField field) {
    searchableInputFields.add(field);
  }

  /**
   * Add a new variable of type {@link osmo.tester.model.data.SearchableInput} to the model.
   *
   * @param input to add.
   */
  public void addSearchableInput(SearchableInput input) {
    searchableInputs.add(input);
  }

  public Requirements getRequirements() {
    return requirements;
  }

  public void setRequirements(Requirements requirements) {
    this.requirements = requirements;
  }

  /**
   * Initialize the test suite, adding observers to capture data from all registered 
   * {@link osmo.tester.model.data.SearchableInput} variables.
   * Also sets the scripted value options for data if defined.
   *
   * @param config This is where the scripter and value options are taken.
   */
  public void initSearchableInputs(OSMOConfiguration config) {
    /* Scripter used for SearchableInput variables. */
    ScriptedValueProvider scripter = config.getScripter();
    //initial capture to allow FSM to have names, etc. for algorithm initialization
    captureSearchableInputs();

    if (scripter != null) {
      initScripts(scripter);
    }
  }

  private void captureSearchableInputs() {
    Collection<SearchableInputField> inputs = new ArrayList<>();
    inputs.addAll(searchableInputFields);
    clearSearchableInputs();
    for (SearchableInputField input : inputs) {
      addSearchableInput(input.getInput());
    }
  }

  private Collection<String> initScripts(ScriptedValueProvider scripter) {
    Map<String, ValueSet<String>> scripts = scripter.getScripts();
    log.debug("scripts loaded:" + scripts);
    Collection<String> errors = new ArrayList<>();
    for (String variable : scripts.keySet()) {
      boolean found = false;
      for (SearchableInputField input : searchableInputFields) {
        if (input.getName().equals(variable)) {
          found = true;
          break;
        }
      }
      if (!found) {
        errors.add(variable);
      }
    }
    if (errors.size() > 0) {
      throw new IllegalArgumentException("Scripted variable(s) not searchable in the model:" + errors);
    }
    return scripts.keySet();
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

  public InvocationTarget getStateNameFor(String modelObjectName) {
    return stateNames.get(modelObjectName);
  }

  public void addStateNameFor(String modelObjectName, InvocationTarget stateName) {
    this.stateNames.put(modelObjectName, stateName);
  }

  public Map<String, InvocationTarget> getStateNames() {
    return stateNames;
  }
}
