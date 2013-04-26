package osmo.tester.reporting.coverage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.scripter.robotframework.CSSHelper;
import osmo.tester.coverage.TestCoverage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * This class provides means to generate coverage metric reports from generated tests.
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public abstract class CoverageMetric {
  /** The tests to report. */
  protected final Collection<TestCase> tests;
  /** Coverage for the tests. */
  private final TestCoverage suiteCoverage;
  /** The parsed model for test generation. */
  protected final FSM fsm;
  /** For template->report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();

  public CoverageMetric(Collection<TestCase> tests, FSM fsm) {
    this.fsm = fsm;
    this.tests = tests;
    this.suiteCoverage = new TestCoverage().addAll(tests);
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }

  /**
   * Count the number of times a transition has been covered.
   *
   * @return the counts.
   */
  protected List<ValueCount> countTransitions() {
    Map<String, Integer> covered = suiteCoverage.getTransitionCoverage();
    List<ValueCount> counts = new ArrayList<>();

    Collection<FSMTransition> all = fsm.getTransitions();
    //make sure every possible transition is there even if never covered
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
   * Count the number of times each transition pair has been covered.
   * A transition pair is A->B, meaning that transition B was taken after A.
   * If the number of times taken is 0, the value 0 is given for that pair.
   * Thus, each pair should be represented in the result(s).
   *
   * @return List defining how often each pair has been taken so far.
   */
  protected List<ValueCount> countTransitionPairs() {
    Map<String, Integer> coverage = new HashMap<>();

    for (TestCase tc : tests) {
      String previous = FSM.START_NAME;
      for (TestStep ts : tc.getSteps()) {
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

    List<String> allPairs = getTransitionPairs();
    for (String pair : allPairs) {
      if (!coverage.containsKey(pair)) {
        coverage.put(pair, 0);
      }
    }
//    Collection<FSMTransition> all = fsm.getTransitions();
//    for (FSMTransition t1 : all) {
//      for (FSMTransition t2 : all) {
//        String pair = t1.getStringName() + "->" + t2.getStringName();
//        if (!coverage.containsKey(pair)) {
//          coverage.put(pair, 0);
//        }
//      }
//    }
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
    Map<String, Integer> coverage = new HashMap<>();

    for (TestCase tc : tests) {
      for (TestStep ts : tc.getSteps()) {
        Collection<String> keys = ts.getCoveredRequirements();

        for (String key : keys) {
          Integer count = coverage.get(key);
          if (count == null)
            count = 0;
          coverage.put(key, count + 1);
        }
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
   * Creates a transition coverage count table, showing how many times each transition
   * has been covered by test cases in test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Transition coverage formatted with given template.
   */
  public String getTransitionCounts(String templateName) {
    List<ValueCount> counts = countTransitions();

    vc.put("transitions", counts);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  /**
   * Creates a transition pair coverage count table, showing how many times each transition pair
   * has been covered by test cases in test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Transition pair coverage formatted with given template.
   */
  public String getTransitionPairCounts(String templateName) {
    List<ValueCount> tpc = countTransitionPairs();
    Collections.sort(tpc);

    vc.put("pairs", tpc);

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
    List<String> transitions = getTransitions();
    List<String> pairs = getTransitionPairs();
    List<String> reqs = getRequirements();
    List<String> variables = getVariables();
    List<VariableValues> variableValues = getVariableValues();

    vc.put("alt", new CSSHelper());
    vc.put("tests", tc);
    vc.put("transition_names", transitions);
    vc.put("req_names", reqs);
    vc.put("transition_pair_names", pairs);
    vc.put("variable_names", variables);
    vc.put("variable_values", variableValues);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  private List<String> getVariables() {
    List<String> variables = new ArrayList<>();
    variables.addAll(suiteCoverage.getVariables().keySet());
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
   * Provides a list of all transitions in the active model objects.
   *
   * @return The transition names.
   */
  private List<String> getTransitions() {
    List<String> result = new ArrayList<>();
    Collection<FSMTransition> transitions = fsm.getTransitions();
    for (FSMTransition transition : transitions) {
      result.add(transition.getStringName());
    }
    Collections.sort(result);
    return result;
  }

  /**
   * Provides a list of transition pairs.
   *
   * @return The pair names, with transitions separated by "->".
   */
  private List<String> getTransitionPairs() {
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
   * Gives a list of all variables counted in test coverage. Could be some state variables, searchable ones, and
   * custom combinations of those as defined for coverage tracking.
   *
   * @return The variable names from test coverage.
   */
  private List<VariableValues> getVariableValues() {
    List<VariableValues> result = new ArrayList<>();
    Map<String, Collection<String>> variables = suiteCoverage.getVariables();
    List<String> coverageNames = new ArrayList<>();
    coverageNames.addAll(variables.keySet());
    Collections.sort(coverageNames);
    for (String name : coverageNames) {
      result.add(new VariableValues(name, variables.get(name)));
    }
    return result;
  }

  /**
   * Write given string to a file with given name. Used to write coverage reports to files.
   *
   * @param text     The report to write.
   * @param fileName Name of the file where to write it.
   * @throws java.io.IOException If something goes wrong with the file access.
   */
  public static void write(String text, String fileName) throws IOException {
    File file = new File(fileName);
    File parent = file.getParentFile();
    if (parent != null) {
      parent.mkdirs();
    }
    FileOutputStream out = new FileOutputStream(file);
    out.write(text.getBytes());
  }
}
