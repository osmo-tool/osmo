package osmo.tester.reporting.coverage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.scripter.robotframework.CSSHelper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * This class provides means to generate coverage metric reports from generated tests.
 *
 * @author Teemu Kanstrén, Olli-Pekka Puolitaival
 */
public abstract class CoverageMetric {
  /** Coverage for the tests. */
  protected final TestCoverage suiteCoverage;
  /** The parsed model for test generation. */
  protected final FSM fsm;
  protected final Collection<TestCase> tests;
  /** For template to report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();

  public CoverageMetric(TestCoverage suiteCoverage, Collection<TestCase> tests, FSM fsm) {
    this.fsm = fsm;
    this.suiteCoverage = suiteCoverage;
    this.tests = tests;
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }

  /**
   * Count the number of times a step has been covered.
   *
   * @return the counts.
   */
  protected List<ValueCount> countSteps() {
    Map<String, Integer> covered = suiteCoverage.getStepCoverage();
    List<ValueCount> counts = new ArrayList<>();

    Collection<FSMTransition> all = fsm.getTransitions();
    //make sure every possible step is there even if never covered
    for (FSMTransition t : all) {
      String tn = t.getStringName();
      if (!covered.containsKey(tn)) {
        covered.put(tn, 0);
      }
    }

    for (Map.Entry<String, Integer> a : covered.entrySet()) {
      ValueCount count = new ValueCount(a.getKey(), a.getValue());
      counts.add(count);
    }
    Collections.sort(counts);

    return counts;
  }

  /**
   * Count the number of times each step pair has been covered.
   * A step pair is A-{@literal >}B, meaning that step B was taken after A.
   * If the number of times taken is 0, the value 0 is given for that pair.
   * Thus, each pair should be represented in the result(s).
   *
   * @return List defining how often each pair has been taken so far.
   */
  protected List<ValueCount> countStepPairs() {
    Map<String, Integer> coverage = new LinkedHashMap<>();

    for (TestCase tc : tests) {
      String previous = FSM.START_STEP_NAME;
      for (TestCaseStep ts : tc.getSteps()) {
        String next = ts.getName();
        String key = previous + "->" + next;
        Integer count = coverage.get(key);
        if (count == null) {
          count = 0;
        }
        coverage.put(key, count + 1);
        previous = ts.getName();
      }
    }

    List<String> allPairs = getStepPairs();
    for (String pair : allPairs) {
      if (!coverage.containsKey(pair)) {
        coverage.put(pair, 0);
      }
    }
    List<ValueCount> tpc = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : coverage.entrySet()) {
      String pair = entry.getKey();
      ValueCount count = new ValueCount(pair, entry.getValue());
      tpc.add(count);
    }
    return tpc;
  }

  /**
   * Counts the number of times each requirement has been covered.
   * If the requirement has not been covered at all, value of 0 is inserted.
   * Thus, all requirements should be listed in the results.
   *
   * @return The coverage count for each requirement.
   */
  protected List<RequirementCount> countRequirements() {
    Map<String, Integer> coverage = new LinkedHashMap<>();

    for (TestCase tc : tests) {
      Collection<String> reqs = tc.getCoverage().getRequirements();
      for (String req : reqs) {
        Integer count = coverage.get(req);
        if (count == null)
          count = 0;
        coverage.put(req, count + 1);
      }
    }

    Requirements requirements = fsm.getRequirements();
    Collection<String> all = requirements.getRequirements();
    for (String name : all) {
      if (!coverage.containsKey(name)) {
        coverage.put(name, 0);
      }
    }

    List<RequirementCount> rc = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : coverage.entrySet()) {
      String name = entry.getKey();
      RequirementCount count = new RequirementCount(name, entry.getValue());
      rc.add(count);
    }
    return rc;
  }

  /**
   * Creates a step coverage count table, showing how many times each step has been taken in the test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Step coverage formatted with given template.
   */
  public String getStepCounts(String templateName) {
    List<ValueCount> counts = countSteps();

    vc.put("steps", counts);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  /**
   * Creates a step pair coverage count table, showing how many times each step pair has been taken in the test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Step pair coverage formatted with given template.
   */
  public String getStepPairCounts(String templateName) {
    List<ValueCount> tpc = countStepPairs();
    Collections.sort(tpc);

    vc.put("step-pairs", tpc);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  /**
   * Creates a requirements coverage count table, showing how many times each requirement
   * has been covered by test cases in test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Requirements coverage formatted with given template.
   */
  public String getRequirementCounts(String templateName) {
    List<RequirementCount> tpc = countRequirements();
    Collections.sort(tpc);

    vc.put("reqs", tpc);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  /**
   * Creates a traceability matrix based on the given Velocity template.
   * The matrix shows how many times each test case has covered the different coverage measure.
   *
   * @param templateName The name of the Velocity template to user for the report.
   * @return The formatted traceability matrix.
   */
  public String getTraceabilityMatrix(String templateName) {
    List<SingleTestCoverage> tc = getTestCoverage();
    List<String> steps = getSteps();
    List<String> pairs = getStepPairs();
    List<String> reqs = getRequirements();
    List<String> variables = getVariables();
    //makes no sense to put all variable values and states there as they may be way too many
    List<VariableValues> variableValues = getVariableValues();
    List<VariableValues> states = getStates();
    List<VariableValues> statePairs = getStatePairs();

    vc.put("alt", new CSSHelper());
    vc.put("tests", tc);
    vc.put("req_names", reqs);
    vc.put("step_names", steps);
    vc.put("step_pair_names", pairs);
    vc.put("variable_names", variables);
    vc.put("variable_values", variableValues);
    vc.put("states", states);
    vc.put("state_pairs", statePairs);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  private List<String> getVariables() {
    List<String> variables = new ArrayList<>();
    variables.addAll(suiteCoverage.getVariables());
    Collections.sort(variables);
    return variables;
  }

  private List<SingleTestCoverage> getTestCoverage() {
    List<SingleTestCoverage> result = new ArrayList<>();
    for (TestCase test : tests) {
      result.add(new SingleTestCoverage(test));
    }
    return result;
  }

  /**
   * Provides a list of all steps in the active model objects.
   *
   * @return The step names.
   */
  private List<String> getSteps() {
    List<String> result = new ArrayList<>();
    Collection<FSMTransition> transitions = fsm.getTransitions();
    for (FSMTransition transition : transitions) {
      result.add(transition.getStringName());
    }
    Collections.sort(result);
    return result;
  }

  /**
   * Provides a list of step pairs.
   *
   * @return The pair names, with steps separated by "-{@literal >}".
   */
  private List<String> getStepPairs() {
    List<String> result = new ArrayList<>();
    Collection<String> pairs = suiteCoverage.getStepPairs();
    result.addAll(pairs);
    Collections.sort(result);
    return result;
  }

  /**
   * Get the list of requirements defined in the model and/or covered by executed test cases.
   *
   * @return The requirement names.
   */
  private List<String> getRequirements() {
    Collection<String> temp = new LinkedHashSet<>();
    Requirements fsmReqs = fsm.getRequirements();
    temp.addAll(fsmReqs.getRequirements());
    temp.addAll(fsmReqs.getExcess());
    List<String> reqs = new ArrayList<>();
    reqs.addAll(temp);
    Collections.sort(reqs);
    return reqs;
  }

  /**
   * Gives a list of all model variables recorded in test coverage.
   *
   * @param variables Get the values from here.
   * @return The variable names and values from test coverage.
   */
  private List<VariableValues> getVariableValues(Map<String, Collection<String>> variables) {
    List<VariableValues> result = new ArrayList<>();
    List<String> coverageNames = new ArrayList<>();
    coverageNames.addAll(variables.keySet());
    Collections.sort(coverageNames);
    for (String name : coverageNames) {
      result.add(new VariableValues(name, variables.get(name)));
    }
    return result;
  }

  public List<VariableValues> getVariableValues() {
    return getVariableValues(suiteCoverage.getVariableValues());
  }

  public List<VariableValues> getStates() {
    return getVariableValues(suiteCoverage.getStates());
  }

  public List<VariableValues> getStatePairs() {
    return getVariableValues(suiteCoverage.getStatePairs());
  }
}
