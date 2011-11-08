package osmo.tester.optimizer.online;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** @author Teemu Kanstren */
public class Candidate {
  private static final Logger log = new Logger(Candidate.class);
  private List<TestCase> tests = new ArrayList<TestCase>();
  private int fitness = -1;
  private final SearchConfiguration config;

  public Candidate(SearchConfiguration config, List<TestCase> tests) {
    this.tests = tests;
    this.config = config;
  }

  public synchronized int getFitness() {
    if (fitness < 0) {
      fitness = calculateFitness();
    }
    return fitness;
  }

  //todo: check for duplicate names of variables in parser
  public int calculateFitness() {
    Collection<String> transitions = new ArrayList<String>();
    Collection<String> pairs = new HashSet<String>();
    Collection<String> singles = new HashSet<String>();
    Map<String, ModelVariable> variables = new HashMap<String, ModelVariable>();
    for (TestCase tc : tests) {
      Collection<TestStep> steps = tc.getSteps();
      String previous = "init";
      for (TestStep step : steps) {
        String transition = step.getTransition().getName();
        transitions.add(transition);
        pairs.add(previous + "_" + transition);
        singles.add(transition);
        previous = transition;
      }
      Map<String, ModelVariable> tcVariables = tc.getVariables();
      for (String variableName : tcVariables.keySet()) {
        ModelVariable var = variables.get(variableName);
        if (var == null) {
          var = new ModelVariable(variableName);
          var.enableMerging();
          variables.put(variableName, var);
        }
        var.addAll(tcVariables.get(variableName));
      }
    }
    log.debug("pairs:" + pairs);

    int fitness = transitions.size() * config.getLengthWeight();
    fitness += singles.size() * config.getTransitionWeight();
    fitness += pairs.size() * config.getPairsWeight();
    fitness += variables.size() * config.getVariableWeight();
    for (ModelVariable variable : variables.values()) {
      fitness += variable.getValues().size() * config.getValueWeight();
    }
    return fitness;
  }

  public int size() {
    return tests.size();
  }

  public List<TestCase> getTests() {
    return tests;
  }

  @Override
  public String toString() {
    return "candidate:fitness="+getFitness();
  }

  public TestCase get(int i) {
    return tests.get(i);
  }
}
