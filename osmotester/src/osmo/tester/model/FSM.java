package osmo.tester.model;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
  private Map<String, FSMTransition> transitions = new HashMap<String, FSMTransition>();
  /** List of generic guards that apply to all transitions. */
  private Collection<InvocationTarget> genericGuards = new ArrayList<InvocationTarget>();
  /** List of generic pre-methods that apply to all transitions. */
  private Collection<InvocationTarget> genericPre = new ArrayList<InvocationTarget>();
  /** List of generic post-methods that apply to all transitions. */
  private Collection<InvocationTarget> genericPost = new ArrayList<InvocationTarget>();
  /** List of methods to be executed before each test case. */
  private Collection<InvocationTarget> befores = new ArrayList<InvocationTarget>();
  /** List of methods to be executed after each test case. */
  private Collection<InvocationTarget> afters = new ArrayList<InvocationTarget>();
  /** List of methods to be executed before the overall test suite. */
  private Collection<InvocationTarget> beforeSuites = new ArrayList<InvocationTarget>();
  /** List of methods to be executed after the overall test suite. */
  private Collection<InvocationTarget> afterSuites = new ArrayList<InvocationTarget>();
  /** List of conditions when to stop (prematurely) test generation (single test, not suite). */
  private Collection<InvocationTarget> endConditions = new ArrayList<InvocationTarget>();
  /** List of conditions when the models allows to stop test generation. */
  private Collection<InvocationTarget> endStates = new ArrayList<InvocationTarget>();
  /** List of state variables to store for each test step. */
  private Collection<VariableField> stateVariables = new ArrayList<VariableField>();
  /** The generated test suite (or one being generated). */
  private final TestSuite suite;
  /** The list of requirements that needs to be covered. */
  private Requirements requirements;

  /**
   * Constructor.
   */
  public FSM(TestSuite suite) {
    this.suite = suite;
  }

  /**
   * Returns an existing object for the requested transition name or creates a new one if one was not previously
   * found existing.
   *
   * @param name The name of the transition. Taken from @Transition("name").
   * @param weight The weight of the transition. Taken from @Transition(weight=x).
   * @return A transition object for the requested name.
   */
  public FSMTransition createTransition(String name, int weight) {
    log.debug("Creating transition: "+name+" weight:"+weight);
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
   * -Check that each @Guard and @Oracle has a matching transition.
   * Note that most checks for specific annotations are done already in the associated
   * {@link osmo.tester.parser.AnnotationParser} object.
   *
   * @param errors Previously defined errors to be reported in addition to new ones found.
   */
  public void checkAndUpdateGenericItems(String errors) {
    log.debug("Checking FSM validity");
    if (requirements == null) {
      log.debug("No requirements object defined. Creating new.");
      //user the setRequirements method to also initialize the requirements object missing state
      setRequirements(new Requirements());
    }
    if (transitions.size() == 0) {
      errors += "No transitions found in given model object. Model cannot be processed.\n";
    }
    for (FSMTransition transition : transitions.values()) {
      InvocationTarget target = transition.getTransition();
      String name = transition.getName();
      log.debug("Checking transition:"+ name);
      if (target == null) {
        errors += "Guard/Pre/Post without transition:"+ name +"\n";
        log.debug("Error: Found guard/pre/post without a matching transition - "+ name);
      } else if (target.getMethod().getParameterTypes().length > 0) {
        Method method = target.getMethod();
        int p = method.getParameterTypes().length;
        errors += "Transition methods are not allowed to have parameters: \""+method.getName()+"()\" has "+p+" parameters.\n";
        log.debug("Error: Found transition with invalid parameters - "+ name);
      }
      errors = addGenericGuardsAndOracles(transition, errors);
    }
    if (errors.length() > 0) {
      throw new IllegalStateException("Invalid FSM:\n"+errors);
    }
    log.debug("FSM checked");
  }

  /**
   * Add generic guards and oracles to all transitions.
   *
   * @param transition The transition to check.
   * @param errors The current error message string.
   * @return The error msg string given with possible new errors appended.
   */
  private String addGenericGuardsAndOracles(FSMTransition transition, String errors) {
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

  public FSMTransition getTransition(String name) {
    return transitions.get(name);
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

  public Requirements getRequirements() {
    return requirements;
  }

  public Collection<InvocationTarget> getEndConditions() {
    return endConditions;
  }

  public Collection<InvocationTarget> getEndStates() {
    return endStates;
  }

  /**
   * Sets the Requirements object, either from the parser if it found one in the model object or from this class
   * if not. Also initialized the requirements object to contain the {@link TestSuite} object for storing and
   * comparing covered requirements.
   *
   * @param requirements The requirements object for defining requirements that should be covered.
   */
  public void setRequirements(Requirements requirements) {
    this.requirements = requirements;
    requirements.setTestSuite(suite);
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
   * Add an end condition that should be checked for test case generation termination condition.
   *
   * @param target The end condition.
   */
  public void addEndCondition(InvocationTarget target) {
    endConditions.add(target);
  }

  /**
   * Add an end condition that should be checked before terminating a test case.
   *
   * @param target The end condition.
   */
  public void addEndState(InvocationTarget target) {
    endStates.add(target);
  }

  /**
   * Add a state variable field for which to store value in each test step.
   *
   * @param var The variable field itself.
   */
  public void addVariable(VariableField var) {
    stateVariables.add(var);
  }

  public Collection<VariableField> getStateVariables() {
    return stateVariables;
  }
}
