package osmo.tester.optimizer.online;

import osmo.tester.generator.testsuite.ModelVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** @author Teemu Kanstren */
public class TestCoverage {
  Collection<String> transitions = new ArrayList<String>();
  Collection<String> pairs = new HashSet<String>();
  Collection<String> singles = new HashSet<String>();
  Collection<String> requirements = new HashSet<String>();
  Map<String, ModelVariable> variables = new HashMap<String, ModelVariable>();

  public ModelVariable getVariable(String name) {
    ModelVariable var = variables.get(name);
    if (var == null) {
      var = new ModelVariable(name);
      //we only come here in case of uncovered variables, which can only be @Variable tagged
//      var.enableMerging();
      variables.put(name, var);
    }
    return var;
  }

  public void addTransition(String transition) {
    transitions.add(transition);
    singles.add(transition);
  }

  public void addPair(String pair) {
    pairs.add(pair);
  }

  public void addRequirements(Collection<String> requirements) {
    requirements.addAll(requirements);
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
}
