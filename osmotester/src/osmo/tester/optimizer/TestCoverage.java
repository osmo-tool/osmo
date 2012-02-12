package osmo.tester.optimizer;

import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** @author Teemu Kanstren */
public class TestCoverage {
  private Collection<String> transitions = new ArrayList<>();
  private Collection<String> pairs = new HashSet<>();
  private Collection<String> singles = new HashSet<>();
  private Collection<String> requirements = new HashSet<>();
  private Map<String, ModelVariable> variables = new HashMap<>();

  public TestCoverage() {
  }

  public void addTestCoverage(TestCase tc) {
    addTransitions(tc.getAllTransitionNames());
    addVariables(tc);
    requirements.addAll(tc.getUniqueRequirementsCoverage());
  }

  public TestCoverage(TestCase tc) {
    addTestCoverage(tc);
  }

  private void addVariables(TestCase tc) {
    Map<String, ModelVariable> tcVariables = tc.getVariables();
    for (String name : tcVariables.keySet()) {
      ModelVariable clone = variables.get(name);
      if (clone == null) {
        clone = new ModelVariable(name);
      }
      ModelVariable var = tcVariables.get(name);
      clone.addAll(var);

      variables.put(name, clone);
    }
  }

  private void addTransitions(Collection<String> names) {
    transitions.addAll(names);
    singles.addAll(names);
    String previous = "init";
    for (String name : names) {
      pairs.add(previous + "-" + name);
      previous = name;
    }
  }

  public Collection<String> getTransitions() {
    return transitions;
  }

  public Collection<String> getPairs() {
    return pairs;
  }

  public Collection<String> getSingles() {
    return singles;
  }

  public Collection<String> getRequirements() {
    return requirements;
  }

  public Map<String, ModelVariable> getVariables() {
    return variables;
  }

  public int fitnessFor(SearchConfiguration config) {
    int fitness = transitions.size() * config.getLengthWeight();
    fitness += singles.size() * config.getTransitionWeight();
    fitness += pairs.size() * config.getPairsWeight();
    fitness += variables.size() * config.getVariableWeight();
    for (ModelVariable variable : variables.values()) {
      Collection<Object> values = new HashSet<>();
      values.addAll(variable.getValues());
      fitness += values.size() * config.getValueWeight();
    }
    fitness += requirements.size() * config.getRequirementWeight();
    return fitness;
  }
}
