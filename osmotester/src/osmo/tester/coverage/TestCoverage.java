package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Represents test coverage for a set of test cases.
 * User to calculate coverage scores for test suite optimization.
 *
 * @author Teemu Kanstren
 */
public class TestCoverage {
  private static final Logger log = new Logger(TestCoverage.class);
  /** The list of transitions covered, in order, including duplicates. */
  private Collection<String> transitions = new ArrayList<>();
  /** The set of step pairs covered. */
  private Collection<String> stepPairs = new LinkedHashSet<>();
  /** The unique set of transitions covered. */
  private Collection<String> singles = new LinkedHashSet<>();
  /** The set of covered requirements. */
  private Collection<String> reqs = new LinkedHashSet<>();
  /** The set of covered variables. */
  private Collection<String> variables = new LinkedHashSet<>();
  /** Set of values covered for different model data variables. */
  private Map<String, Collection<String>> values = new LinkedHashMap<>();
  /** Set of covered states. */
  private Collection<String> states = new LinkedHashSet<>();
  /** Set of covered transitions between states. */
  private Collection<String> statePairs = new LinkedHashSet<>();

  /**
   * Start with an empty set.
   */
  public TestCoverage() {
  }

  /**
   * Start with the given set.
   *
   * @param tests  Add coverage from all these tests.
   */
  public TestCoverage(Collection<TestCase> tests) {
    for (TestCase test : tests) {
      addTestCoverage(test);
    }
  }

  public Collection<String> getStates() {
    Collection<String> clone = new ArrayList<>();
    clone.addAll(states);
    return clone;
  }

  public Collection<String> getStatePairs() {
    Collection<String> clone = new ArrayList<>();
    clone.addAll(statePairs);
    return clone;
  }

  /**
   * Initialized with coverage for a single test case.
   *
   * @param test   The test covered.
   */
  public TestCoverage(TestCase test) {
    addTestCoverage(test);
  }
  
  /**
   * Gives all transitions in this test suite, including coverage number.
   * Coverage number tells how many times transition is taken in this test suite
   *
   * @return The transitions with coverage number
   */
  public Map<String, Integer> getTransitionCoverage() {
    Map<String, Integer> result = new HashMap<>();
    for (String transition : transitions) {
      Integer count = result.get(transition);
      if (count == null) {
        count = 0;
      }
      result.put(transition, count + 1);
    }
    return result;
  }

  /**
   * Add the coverage in the given test case.
   *
   * @param test The test to add.
   */
  public synchronized void addTestCoverage(TestCase test) {
    addTestCoverage(test, test.getAllTransitionNames().size());
  }

  /**
   * Adds coverage for all the given tests.
   * 
   * @param tests To add.
   * @return Self reference.
   */
  public TestCoverage addAll(Collection<TestCase> tests) {
    for (TestCase test : tests) {
      addTestCoverage(test);
    }
    return this;
  }

  /**
   * Add the coverage in the given test case up to a given number of steps in the test case.
   * Useful for calculating what would be the added coverage is different number of steps was added.
   * Especially when comparing different paths to each other.
   *
   * @param test      The test containing the steps to add.
   * @param stepCount The number of steps to take from the test.
   */
  public synchronized void addTestCoverage(TestCase test, int stepCount) {
    Collection<String> names = new ArrayList<>();

    int count = 0;
    String previousState = "osmo.start.state";
    for (TestCaseStep step : test.getSteps()) {
      String name = step.getName();
      names.add(name);
      String state = step.getState();
      if (state != null) {
        //we ignore null so if there is no state we do not mess calculations for coverage score
        states.add(state);
        statePairs.add(previousState+"->"+state);
        previousState = state;
      }
      addValues(step);
      reqs.addAll(step.getCoveredRequirements());
      count++;
      if (count == stepCount) {
        break;
      }
    }
    addSteps(names);
    log.debug("added coverage for " + stepCount + " steps in " + test);
  }

  /**
   * Adds the values for the variables in the given test case to the covered set.
   *
   * @param step This is where we take the steps from.
   */
  private synchronized void addValues(TestCaseStep step) {
    Collection<ModelVariable> toAdd = step.getValues();
    for (ModelVariable variable : toAdd) {
      String name = variable.getName();
      variables.add(name);
      Collection<String> values = this.values.get(name);
      if (values == null) {
        values = new LinkedHashSet<>();
      }
      for (Object value : variable.getValues()) {
        values.add("" + value);
      }

      this.values.put(name, values);
    }
  }

  /**
   * Add the set of given transitions to the sets representing various aspects of covered transitions.
   * That is transition list, unique single transitions, and transition pairs.
   *
   * @param names The names of transitions to add.
   */
  private synchronized void addSteps(Collection<String> names) {
    transitions.addAll(names);
    singles.addAll(names);
    String previous = FSM.START_NAME;
    for (String name : names) {
      stepPairs.add(previous + "->" + name);
      previous = name;
    }
  }

  public Collection<String> getTransitions() {
    return transitions;
  }

  public Collection<String> getStepPairs() {
    return stepPairs;
  }

  public Collection<String> getSingles() {
    return singles;
  }

  public Collection<String> getRequirements() {
    return reqs;
  }

  public Collection<String> getVariables() {
    return variables;
  }

  public Map<String, Collection<String>> getValues() {
    return values;
  }

  /**
   * Creates a clone of this coverage set.
   * The clone can then be modified without affecting the original set (no reference to same internal lists etc.).
   *
   * @return The clone object.
   */
  public synchronized TestCoverage cloneMe() {
    TestCoverage clone = new TestCoverage();
    clone.stepPairs.addAll(stepPairs);
    clone.transitions.addAll(transitions);
    clone.reqs.addAll(reqs);
    clone.singles.addAll(singles);
    clone.states.addAll(states);
    clone.statePairs.addAll(statePairs);
    clone.variables.addAll(variables);
    for (String key : values.keySet()) {
      Collection<String> values = new LinkedHashSet<>();
      values.addAll(this.values.get(key));
      clone.values.put(key, values);
    }
    return clone;
  }

  @Override
  public String toString() {
    return "TestCoverage{" +
            "transitions=" + transitions +
            ", stepPairs=" + stepPairs +
            ", singles=" + singles +
            ", requirements=" + reqs +
            ", states=" + states +
            ", statePairs=" + statePairs +
            '}';
  }

  public String coverageString(FSM fsm, Collection<String> possibleStepPairs, Collection<String> possibleStates, Collection<String> possibleStatePairs, boolean printAll) {
    String result = "Covered elements:\n";
    result += "Total steps: "+transitions.size();
    result += "\nUnique steps: "+singles.size();
    if (fsm != null) {
      Collection<FSMTransition> fsmTransitions = fsm.getTransitions();
      int fsmMax = fsmTransitions.size();
      result += (" (of "+ fsmMax +")");
      if (fsmMax > singles.size()) {
        Collection<String> all = new HashSet<>();
        for (FSMTransition ft : fsmTransitions) {
          all.add(ft.getStringName());
        }
        all.removeAll(singles);
        result += " missing:"+all;
      }
    }
    result += "\nUnique step-pairs: "+stepPairs.size();
    if (possibleStepPairs != null) {
      Collection<String> all = new HashSet<>();
      all.addAll(possibleStepPairs);
      all.removeAll(stepPairs);
      result += " (of "+possibleStepPairs.size()+")";
      if (printAll || (all.size() > 0 && all.size() < 5)) {
        result += " missing:"+all;
      }
    }
    
    result += "\nUnique requirements: "+reqs.size();
    result += "\nUnique states: "+states.size();
    if (possibleStates != null) {
      Collection<String> all = new HashSet<>();
      all.addAll(possibleStates);
      all.removeAll(states);
      result += " (of "+possibleStates.size()+")";
      if (all.size() > 0 && all.size() < 5) {
        result += " missing:"+all;
      }
    }
    result += "\nUnique state-pairs: "+statePairs.size();
    if (possibleStatePairs != null) {
      Collection<String> all = new HashSet<>();
      all.addAll(possibleStatePairs);
      all.removeAll(statePairs);
      result += " (of "+possibleStatePairs.size()+")";
      if (printAll || (all.size() > 0 && all.size() < 5)) {
        result += " missing:"+all;
      }
    }
    return result;
  }

  public void removeAll(TestCoverage in) {
    transitions.removeAll(in.transitions);
    singles.removeAll((in.singles));
    stepPairs.removeAll(in.stepPairs);
    states.removeAll(in.states);
    statePairs.removeAll(in.statePairs);
    reqs.removeAll(in.reqs);
    variables.removeAll(in.variables);
    for (String var : values.keySet()) {
      Collection<String> yourValues = in.values.get(var);
      if (yourValues == null) continue;
      Collection<String> myValues = values.get(var);
      myValues.removeAll(yourValues);
    }
  }
}
