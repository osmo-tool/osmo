package osmo.tester.reporting.coverage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.model.VariableField;
import osmo.tester.model.dataflow.SearchableInput;
import osmo.tester.scripter.robotframework.CSSHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class provides means to generate coverage metric reports from generated tests.
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public abstract class CoverageMetric {
  /** The test suite generation history. */
  protected final TestSuite testSuite;
  /** The parsed model for test generation. */
  protected final FSM fsm;
  /** For template->report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();

  public CoverageMetric(TestSuite ts, FSM fsm) {
    this.testSuite = ts;
    this.fsm = fsm;
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }

  /**
   * Count the number of times a transition has been covered.
   * 
   * @return Key = transition, Value = number of times covered.
   */
  protected Map<FSMTransition, Integer> countTransitions() {
    Map<FSMTransition, Integer> covered = testSuite.getTransitionCoverage();

    Collection<FSMTransition> all = fsm.getTransitions();
    for (FSMTransition t : all) {
      if (!covered.containsKey(t)) {
        covered.put(t, 0);
      }
    }
    return covered;
  }

  /**
   * Count the number of times each transition pair has been covered.
   * A transition pair is A->B, meaning that transition B was taken after A.
   * If the number of times taken is 0, the value 0 is given for that pair.
   * Thus, each pair should be represented in the result(s).
   * 
   * @return List defining how often each pair has been taken so far.
   */
  protected List<TransitionPairCount> countTransitionPairs() {
    Map<TransitionPair, Integer> coverage = new HashMap<>();

    for (TestCase tc : testSuite.getFinishedTestCases()) {
      FSMTransition previous = new FSMTransition("Start");
      for (TestStep ts : tc.getSteps()) {
        FSMTransition next = ts.getTransition();
        TransitionPair key = new TransitionPair(previous, next);
        Integer count = coverage.get(key);
        if (count == null) {
          count = 0;
        }
        coverage.put(key, count + 1);
        previous = ts.getTransition();
      }
    }

    Collection<FSMTransition> all = fsm.getTransitions();
    for (FSMTransition t1 : all) {
      for (FSMTransition t2 : all) {
        TransitionPair pair = new TransitionPair(t1, t2);
        if (!coverage.containsKey(pair)) {
          coverage.put(pair, 0);
        }
      }
    }

    List<TransitionPairCount> tpc = new ArrayList<>();
    for (Map.Entry<TransitionPair, Integer> entry : coverage.entrySet()) {
      TransitionPair pair = entry.getKey();
      TransitionPairCount count = new TransitionPairCount(pair, entry.getValue());
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

    for (TestCase tc : testSuite.getFinishedTestCases()) {
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
    Map<FSMTransition, Integer> coverage = countTransitions();
    List<TransitionCount> counts = new ArrayList<>();

    for (Map.Entry<FSMTransition, Integer> a : coverage.entrySet()) {
      TransitionCount count = new TransitionCount(a.getKey(), a.getValue());
      counts.add(count);
    }
    Collections.sort(counts);
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
    List<TransitionPairCount> tpc = countTransitionPairs();
    Collections.sort(tpc);

    vc.put("pairs", tpc);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  /**
   * Creates a requirement coverage count table, showing how many times each requirement
   * has been covered by test cases in test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Requirement coverage formatted with given template.
   */
  public String getRequirementsCounts(String templateName) {
    List<RequirementCount> tpc = countRequirements();
    Collections.sort(tpc);

    vc.put("requirements", tpc);

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
    Collections.sort(transitions);
    List<String> pairs = getTransitionPairs();
    Collections.sort(pairs);
    List<String> requirements = getRequirements();
    Collections.sort(requirements);
    List<String> variables = getVariables();
    Collections.sort(variables);

    vc.put("alt", new CSSHelper());
    vc.put("tests", tc);
    vc.put("transition_names", transitions);
    vc.put("requirement_names", requirements);
    vc.put("transition_pair_names", pairs);
    vc.put("variable_names", variables);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }

  private List<SingleTestCoverage> getTestCoverage() {
    List<SingleTestCoverage> result = new ArrayList<>();
    List<TestCase> tests = testSuite.getFinishedTestCases();
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
      result.add(transition.getName());
    }
    return result;
  }

  /**
   * Provides a list of transition pairs.
   * 
   * @return The pair names, with transitions separated by "->".
   */
  private List<String> getTransitionPairs() {
    List<TransitionPairCount> pairCounts = countTransitionPairs();
    List<String> pairs = new ArrayList<>();
    for (TransitionPairCount count : pairCounts) {
      String from = count.getFrom().getName();
      String to = count.getTo().getName();
      pairs.add(from + "->" + to);
    }
    return pairs;
  }

  /**
   * Get the list of requirements defined in the model.
   * 
   * @return The requirement names.
   */
  private List<String> getRequirements() {
    Collection<String> temp = new HashSet<>();
    Requirements fsmRequirements = fsm.getRequirements();
    temp.addAll(fsmRequirements.getRequirements());
    temp.addAll(fsmRequirements.getExcess());
    List<String> requirements = new ArrayList<>();
    requirements.addAll(temp);
    return requirements;
  }

  /**
   * Gives a list of all model variables. Include those tagged as @Variable
   * and those with type of {@link SearchableInput}
   * 
   * @return The variable names.
   */
  private List<String> getVariables() {
    Collection<VariableField> stateVariables = fsm.getStateVariables();
    Collection<SearchableInput> inputs = fsm.getSearchableInputs();
    List<String> result = new ArrayList<>();
    for (VariableField field : stateVariables) {
      result.add(field.getName());
    }
    for (SearchableInput input : inputs) {
      result.add(input.getName());
    }
    return result;
  }

  /**
   * Write given string to a file with given name. Used to write coverage reports to files.
   *
   * @param text The report to write.
   * @param file Name of the file where to write it.
   * @throws IOException If something goes wrong with the file access.
   */
  public void write(String text, String file) throws IOException {
    FileOutputStream out = new FileOutputStream(file);
    out.write(text.getBytes());
  }
}
