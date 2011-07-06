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
  /** List of generic oracles that apply to all transitions. */
  private Collection<InvocationTarget> genericOracles = new ArrayList<InvocationTarget>();
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
  /** The generated test suite (or one being generated). */
  private final TestSuite testSuite = new TestSuite();
  /** The list of requirements that needs to be covered. */
  private Requirements requirements;

  /**
   * Constructor.
   */
  public FSM() {
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
      //we can come here from guard, oracle, or transition creation. however, only transitions define weights
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
   * Checks the FSM for validity. This includes the following constraints:
   * -Is a requirements object defined in the model? if so use it, otherwise create empty one.
   * -Check that each @Guard has a matching transition.
   * -Check that no @Transition method has parameters.
   * -Check that each @Guard returns a boolean value.
   * -Check that no @Guard method has a return value.
   * -Check that each @Oracle has proper parameters and return value.
   * -Check that each @EndCondition has proper parameters and return value.
   * @param errors Previously defined errors to be reported in addition to new ones found.
   */
  public void check(String errors) {
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
        errors += "Guard/Oracle without transition:"+ name +"\n";
        log.debug("Error: Found guard/oracle without a matching transition - "+ name);
      } else if (target.getMethod().getParameterTypes().length > 0) {
        Method method = target.getMethod();
        int p = method.getParameterTypes().length;
        errors += "Transition methods are not allowed to have parameters: \""+method.getName()+"()\" has "+p+" parameters.\n";
        log.debug("Error: Found transition with invalid parameters - "+ name);
      }
      errors = checkGuards(transition, errors);
      errors = checkOracles(transition, errors);
    }
    errors = checkEndConditions(errors);
    if (errors.length() > 0) {
      throw new IllegalStateException("Invalid FSM:\n"+errors);
    }
    log.debug("FSM checked");
  }

  /**
   * Check the guards for the given transition.
   * Since a new transition is previously generated for each guard that previously had no transition defined,
   * a guard without a transition is also checked here as it is simply a {@link FSMTransition} object without
   * the actual transition method defined but with a set of guards defined.
   *
   * @param transition The transition to check.
   * @param errors The current error message string.
   * @return The error msg string given with possible new errors appended.
   */
  private String checkGuards(FSMTransition transition, String errors) {
    //we add all generic guards to the set of guards for this transition. doing it here includes them in the checks
    for (InvocationTarget guard : genericGuards) {
      transition.addGuard(guard);
    }
    for (InvocationTarget guard : transition.getGuards()) {
      Method method = guard.getMethod();
      Class<?> type = method.getReturnType();
      if (!(type.equals(boolean.class))) {
        errors += "Invalid return type for guard (\""+method.getName()+"()\"):"+type+".\n";
      }
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length > 0) {
        errors += "Guard methods are not allowed to have parameters: \""+method.getName()+"()\" has "+parameterTypes.length+" parameters.\n";
      }
    }
    return errors;
  }

  /**
   * Checks all oracles to see that there are no oracles without associated transitions.
   * Also checks that the oracle methods have no parameters.
   *
   * @param transition The transition to check.
   * @param errors The current error message string.
   * @return The error msg string given with possible new errors appended.
   */
  private String checkOracles(FSMTransition transition, String errors) {
    for (InvocationTarget oracle : genericOracles) {
      transition.addOracle(oracle);
    }
    for (InvocationTarget oracle : transition.getOracles()) {
      Method method = oracle.getMethod();
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length > 0) {
        errors += "Oracle methods are not allowed to have parameters: \""+method.getName()+"()\" has "+parameterTypes.length+" parameters.\n";
      }
    }
    return errors;
  }

  /**
   * Check the model end conditions.
   * Each end condition must have a boolean return value and no parameters.
   *
   * @param errors The current error message string.
   * @return The error msg string given with possible new errors appended.
   */
  private String checkEndConditions(String errors) {
    for (InvocationTarget ec : endConditions) {
      Method method = ec.getMethod();
      Class<?> type = method.getReturnType();
      if (!(type.equals(boolean.class))) {
        errors += "Invalid return type for end condition (\""+method.getName()+"()\"):"+type+". Should be boolean.\n";
      }
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length > 0) {
        errors += "End condition methods are not allowed to have parameters: \""+method.getName()+"()\" has "+parameterTypes.length+" parameters.\n";
      }
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

  public TestSuite getTestSuite() {
    return testSuite;
  }

  public Requirements getRequirements() {
    return requirements;
  }

  public Collection<InvocationTarget> getEndConditions() {
    return endConditions;
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
    requirements.setTestSuite(testSuite);
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
   * Add an oracle that should be evaluated for all transitions in the test model.
   *
   * @param target The oracle method to be invoked for evaluation.
   */
  public void addGenericOracle(InvocationTarget target) {
    genericOracles.add(target);
  }

  /**
   * Add an end condition that should be checked for test case generation termination condition.
   *
   * @param target The end condition.
   */
  public void addEndCondition(InvocationTarget target) {
    endConditions.add(target);
  }
}
