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
    Collection<String> requirements = new HashSet<String>();
    Map<String, ModelVariable> variables = new HashMap<String, ModelVariable>();
    for (TestCase tc : tests) {
      TestCoverage coverage = coverageFor(tc);
      transitions.addAll(coverage.getTransitions());
      pairs.addAll(coverage.getPairs());
      singles.addAll(coverage.getSingles());
      requirements.addAll(coverage.getRequirements());
      for (ModelVariable tcVar : tc.getVariables().values()) {
        String name = tcVar.getName();
        ModelVariable var = variables.get(name);
        if (var == null) {
          var = new ModelVariable(name);
          var.enableMerging();
          variables.put(name, var);
        }
        var.addAll(tcVar);
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
    fitness += requirements.size() * config.getRequirementWeight();
    return fitness;
  }

  private TestCoverage coverageFor(TestCase tc) {
    TestCoverage coverage = new TestCoverage();
    Collection<TestStep> steps = tc.getSteps();
    String previous = "init";
    for (TestStep step : steps) {
      String transition = step.getTransition().getName();
      coverage.addTransition(transition);
      coverage.addPair(previous + "_" + transition);
      previous = transition;
    }
    Map<String, ModelVariable> tcVariables = tc.getVariables();
    for (String variableName : tcVariables.keySet()) {
      ModelVariable var = coverage.getVariable(variableName);
      var.addAll(tcVariables.get(variableName));
    }
    coverage.addRequirements(tc.getCoveredRequirements());
    return coverage;
  }

  public int size() {
    return tests.size();
  }

  public List<TestCase> getTests() {
    return tests;
  }

  @Override
  public String toString() {
    return "candidate:fitness=" + getFitness();
  }

  public TestCase get(int i) {
    return tests.get(i);
  }

  public String matrix() {
    String matrix = "";
    for (TestCase test : tests) {
      matrix += matrixFor(test);
    }
    return matrix;
  }

  private String matrixFor(TestCase test) {
    String matrix = "";
    TestCoverage coverage = coverageFor(test);
    int pairs = coverage.getPairs().size();
    int transitions = coverage.getTransitions().size();
    int singles = coverage.getSingles().size();
    int reqs = coverage.getRequirements().size();
    Map<String, ModelVariable> variables = coverage.getVariables();
    int variableCount = variables.keySet().size();
    int valueCount = 0;
    for (ModelVariable variable : variables.values()) {
      valueCount += variable.getValues().size();
    }
    matrix += "test:\n";
    matrix += "pairs = " + pairs + "\n";
    matrix += "transitions = " + transitions + "\n";
    matrix += "singles = " + singles + "\n";
    matrix += "requirements = " + reqs + "\n";
    matrix += "variables = " + variableCount + "\n";
    matrix += "values = " + valueCount + "\n";
    return matrix;
  }
}
