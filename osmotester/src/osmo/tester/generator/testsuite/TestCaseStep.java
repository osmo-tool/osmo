package osmo.tester.generator.testsuite;

import osmo.common.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Describes a single test step in a test case.
 * Difference to {@link osmo.tester.model.FSMTransition} is that this includes the runtime information 
 * such as covered requirements.
 * Model state variable values (annotated with {@link osmo.tester.annotation.Variable}) are stored at the end of
 * the executed transition function.
 *
 * @author Teemu Kanstren
 */
public class TestCaseStep {
  private static Logger log = new Logger(TestCaseStep.class);
  /** The transition that was taken in this test step. */
  private final String transitionName;
  /** The model object from which the transition was executed. */
  private final String modelObjectName;
  /** The set of requirements covered by this test step. */
  private Collection<String> coveredRequirements = null;
  /** The data variables and the values covered for each in this test case. */
  private Map<String, ModelVariable> values = new LinkedHashMap<>();
  /** Step identifier. */
  private final int id;
  /** The parent test case to which this step belongs. */
  private final TestCase parent;
  /** When was the step execution started? */
  private long startTime = 0;
  /** When did the step execution end? */
  private long endTime = 0;
  /** Stores the user defined custom state string when this step was executed. */
  private String state;
  private boolean failed;
  private Map<String, Object> attributes = new HashMap<>();

  /**
   * Constructor.
   *
   * @param transition The transition that was taken in this test step.
   * @param id         The identifier for this step.
   */
  public TestCaseStep(TestCase parent, FSMTransition transition, int id) {
    this.parent = parent;
    this.transitionName = transition.getStringName();
    if (transition.getTransition() == null) {
      log.debug("NULL transition object, assuming unit test in progress..");
      this.modelObjectName = transition.toString();
    } else {
      this.modelObjectName = transition.getModelObjectName();
    }
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return transitionName;
  }

  /**
   * Mark the given requirement as covered by this test step.
   *
   * @param requirement The identifier of the covered requirement.
   */
  public void covered(String requirement) {
    if (coveredRequirements == null) {
      coveredRequirements = new ArrayList<>();
    }
    coveredRequirements.add(requirement);
  }

  /**
   * List of covered requirements by this test step.
   *
   * @return List of covered requirements by this test step.
   */
  public Collection<String> getCoveredRequirements() {
    if (coveredRequirements == null) {
      return Collections.EMPTY_LIST;
    }
    Collection<String> result = new ArrayList<>();
    result.addAll(coveredRequirements);
    return result;
  }

  @Override
  public String toString() {
    return transitionName;
  }

  /**
   * Sets the start time.
   */
  public void start() {
    startTime = System.currentTimeMillis();
  }

  /**
   * Sets the end time.
   */
  public void end() {
    endTime = System.currentTimeMillis();
  }

  /**
   * Stores the general step state. General state refers to the state in the model variables that the generator sees.
   * Another state is the user defined state, which is queried from specifically annotated methods.
   *
   * @param fsm This is where the state is copied from.
   */
  public void storeGeneralState(FSM fsm) {
    Collection<VariableField> variables = fsm.getStateVariables();
    for (VariableField variable : variables) {
      String name = variable.getName();
      Object value = variable.getValue();
      parent.addVariableValue(name, value, true);
    }
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void addVariableValue(String name, Object value) {
    addVariableValue(name, value, false);
  }

  /**
   * Adds a value for model variable. Means a value that was generated.
   *
   * @param name  Name of the variable.
   * @param value The value of the variable.
   */
  public void addVariableValue(String name, Object value, boolean merge) {
//    log.debug("Variable:" + name + " add value:" + value);
    ModelVariable mv = values.get(name);
    if (mv == null) {
      mv = new ModelVariable(name);
      values.put(name, mv);
    }
    mv.addValue(value, merge);
  }

  public Collection<ModelVariable> getValues() {
    return values.values();
  }

  public List<ModelVariable> getHtmlParameters() {
    List<ModelVariable> result = new ArrayList<>();
    int count = 0;
    Map<String, Collection<Object>> all = new LinkedHashMap<>();
    for (ModelVariable value : values.values()) {
      for (Object o : value.getValues()) {
        String name = value.getName();
        ModelVariable var = new ModelVariable(name);
        Collection<Object> added = all.get(name);
        if (added == null) {
          added = new LinkedHashSet<>();
          all.put(name, added);
        }
        if (added.contains(o)) {
          continue;
        }
        var.addValue(o, false);
        added.add(o);
        result.add(var);
        count++;
      }
    }
    int diff = parent.getParameterCount() - count;
    for (int i = 0 ; i < diff ; i++) {
      ModelVariable var = new ModelVariable("");
      var.addValue("", false);
      result.add(var);
    }
    return result;
  }

  public String getModelObjectName() {
    return modelObjectName;
  }

  public String getState() {
    return state;
  }

  public void setUserState(String state) {
    this.state = state;
  }

  public TestCase getParent() {
    return parent;
  }

  public void setFailed(boolean failed) {
    this.failed = failed;
  }

  public boolean isFailed() {
    return failed;
  }
  
  public void setAttribute(String name, Object value) {
    attributes.put(name, value);
  }
  
  public Object getAttribute(String name) {
    return attributes.get(name);
  }
}
