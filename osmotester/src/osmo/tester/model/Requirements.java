package osmo.tester.model;

import osmo.common.log.Logger;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestSuite;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * Used to define a set of requirements that should be covered and to evaluate which ones have been covered overall.
 * Additionally, each {@link osmo.tester.generator.testsuite.TestCaseStep} also defines which requirements have been
 * covered in which test case and test step. A requirement is defined as a string that is expected to be both its
 * unique identifier and expressive enough to describe the actual intent of the requirement for the user to be able
 * to understand what it is. Or you can just put whatever you want in it, of course..
 *
 * @author Teemu Kanstren
 */
public class Requirements {
  private static final Logger log = new Logger(Requirements.class);
  /** The overall set of requirements that should be covered. */
  private List<String> reqs = new ArrayList<>();
  /** The set of requirements that have been covered. */
  private Collection<String> covered = new ArrayList<>();
  /** The set of generated tests cases, including the one currently under generation. */
  private TestSuite testSuite = null;

  public Requirements() {
  }

  public void setTestSuite(TestSuite testSuite) {
    log.debug("Setting test suite:" + testSuite);
    this.testSuite = testSuite;
    fillCoverage(testSuite.getCoverage());
  }
  
  public void fillCoverage(TestCoverage tc) {
    covered.clear();
    covered.addAll(tc.getRequirements());
  }

  /**
   * Adds a new requirement that should be covered.
   *
   * @param requirement The identifier of the requirement.
   */
  public void add(Object requirement) {
    if (requirement == null) {
      log.warn("NULL value given as requirement. Is that intended?");
    }
    String name = ""+requirement;
    //check if already exists
    if (reqs.contains(name)) {
      throw new IllegalArgumentException("Attempted to register '" + name + "' twice. Duplicates not allowed.");
    }
    reqs.add(name);
  }

  /**
   * Marks the given requirement as covered.
   *
   * @param requirement The identifier of the requirement, which will be turned into a string (toString()).
   */
  public void covered(Object requirement) {
    if (requirement == null) {
      log.warn("NULL value given as requirement. Is that intended?");
    }
    covered.add(""+requirement);
    testSuite.coveredRequirement("" + requirement);
  }

  /**
   * Gives the list of requirements defined with the "add" method.
   *
   * @return List of defined requirements.
   */
  public Collection<String> getRequirements() {
    return reqs;
  }

  /**
   * Gives the list of covered requirements defined with the "covered" method. Removes duplicates, keeps order.
   *
   * @return List of covered requirements.
   */
  public Collection<String> getUniqueCoverage() {
    Collection<String> set = new TreeSet<>();
    set.addAll(covered);
    return set;
  }

  /**
   * Gives the list of requirements that have not been covered.
   *
   * @return List of requirements not covered by this suite.
   */
  public Collection<String> getMissingCoverage() {
    Collection<String> set = new TreeSet<>();
    set.addAll(reqs);
    set.removeAll(covered);
    return set;
  }

  /**
   * Gives the list of covered requirements defined with the "covered" method. Keeps duplicates and order.
   *
   * @return List of covered requirements.
   */
  public Collection<String> getFullCoverage() {
    return covered;
  }

  /**
   * Gives the list of excess covered requirements. These are the ones that have been covered (with "covered" method)
   * but were not given as options previously (with the "add" method).
   *
   * @return The number of covered but unexpected requirements.
   */
  public Collection<String> getExcess() {
    Collection<String> excess = new HashSet<>();
    excess.addAll(covered);
    excess.removeAll(reqs);
    return excess;
  }

  /**
   * Checks if the given requirement is covered.
   *
   * @param requirement The name of the requirement to check.
   * @return True if covered, false if not.
   */
  public boolean isCovered(String requirement) {
    return covered.contains(requirement);
  }

  /** This resets the requirements coverage, removing any coverage information. */
  public void clearCoverage() {
    covered.clear();
  }

  /**
   * Creates a string representation of the set of covered requirements vs the set of all defined requirements.
   *
   * @return Message that can be printed to tell the requirements coverage.
   */
  public String printCoverage() {
    if (reqs.size() == 0 && covered.size() == 0) {
      return "No requirements defined. Not calculating or showing requirement coverage. \n" +
              "If you want to see requirement coverage you need to define a @RequirementsField\n" +
              "and add some requirements and their coverage into the model.";
    }
    String result = "";
    Collections.sort(reqs);
    result += "Requirements:" + reqs + "\n";
    Collection<String> uniqueCoverage = getUniqueCoverage();
    result += "Covered:" + uniqueCoverage + "\n";
    result += "Not covered:" + getMissingCoverage() + "\n";
    int n = uniqueCoverage.size() - getExcess().size();
    int total = reqs.size();
    if (total > 0) {
      double p = (double) n / (double) total * 100;
      final MessageFormat format = new MessageFormat("Total unique requirements = {0}/{1} ({2}%) requirements.");
      Object args = new Object[]{n, total, p};
      result += format.format(args);
    } else {
//      out.append("No requirements were defined (with the add() method) so no percentage is calculated.");
    }
    return result;
  }

  public boolean isEmpty() {
    return (reqs.size() == 0 && covered.size() == 0);
  }

  @Override
  public String toString() {
    return "Requirements{" +
            "reqs=" + reqs +
            '}';
  }
}
