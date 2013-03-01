package osmo.tester.generator.endcondition.structure;

import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Defines coverage requirements for test case/test suite related to the model structure.
 * Includes the number of unique test steps covered, the number of unique step-pairs covered, and
 * the number of requirements covered.
 * Typically used for defining a requirement for {@link CoverageEndCondition}.
 * 
 * @author Teemu Kanstren 
 */
public class CoverageRequirement {
  /** Number of unique steps that need to be covered. */
  private int steps = 0;
  /** Number of unique step-pairs that need to be covered. */
  private int pairs = 0;
  /** Number of requirements that need to be covered. */
  private int requirements = 0;

  /**
   * Creates the requirement.
   * 
   * @param steps The number of steps to cover. If negative, the number of all parsed steps will be used.
   * @param pairs The number of step-pairs to cover. If negative, the number of all parsed pairs will be used.
   * @param requirements The number of requirements to cover. Again, model defined number if negative.
   */
  public CoverageRequirement(int steps, int pairs, int requirements) {
    this.steps = steps;
    this.pairs = pairs;
    this.requirements = requirements;
  }

  /**
   * Initialize the information from the parsed model.
   * If number of steps given by user is negative, the number of unique steps from the parsed set is used.
   * If number of step-pairs given by user, a number is calculated as number of parsed steps time two + one.
   * The "+one" part is due to the "start" step being counted as one step. This has the effect that if this
   * is a coverage requirement for a test case, it should have the "+one" in it as the test case has only one
   * starting point. On the other hand, if this is a coverage requirement for a test suite, this should actually
   * before "+number of starting steps". At maximum there are as many possible starting pairs as there are possible 
   * unique steps.
   * Assuming that the starting steps are known..
   * 
   * @param fsm This is where the values are taken from.
   */
  public void init(FSM fsm) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    if (steps < 0) {
      steps = transitions.size();
    }
    if (pairs < 0) {
      List<String> names = new ArrayList<>();
      for (FSMTransition transition : transitions) {
        names.add(transition.getStringName());
      }
      pairs = names.size()*names.size()+1;
    }
    if (requirements < 0) {
      requirements = fsm.getRequirements().getRequirements().size();
    }
  }

  /**
   * Checks the given test suite to see if if fulfills the coverage requirements defined in this class.
   * The suite must have a valid test coverage object attached.
   * 
   * @param suite To check.
   * @return True if coverage satisfied. False if not.
   */
  public boolean checkCoverage(TestSuite suite) {
    TestCoverage coverage = suite.getCoverage();
    if (pairs > 0 && pairs > coverage.getPairs().size()) return false;
    if (requirements > 0 && requirements > coverage.getRequirements().size()) return false;
    if (steps > 0 && steps > coverage.getSingles().size()) return false;
    return true;
  }

  /**
   * Checks the given test case to see if if fulfills the coverage requirements defined in this class.
   *
   * @param test To check.
   * @return True if coverage satisfied. False if not.
   */
  public boolean checkCoverage(TestCase test) {
    if (requirements > 0 && requirements > test.getUniqueRequirementCoverage().size()) return false;
    if (steps > 0 && steps > test.getCoveredTransitions().size()) return false;
    if (pairs > 0 && pairs > countPairs(test.getAllTransitionNames())) return false;
    return true;
  }

  /**
   * Counts the number of unique step-pairs in the given trace.
   * 
   * @param names The generation trace (for a test case).
   * @return The number of unique pairs covered.
   */
  public int countPairs(List<String> names) {
    Collection<String> pairs = new LinkedHashSet<>();
    String previous = "start";
    for (String name : names) {
      String pair = previous+"->"+name;
      pairs.add(pair);
      previous = name;
    }
    return pairs.size();
  }
  
}
