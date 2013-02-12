package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.Requirements;

/**
 * A simple end condition to stop test case generation when all requirements are covered.
 * The requirements to be covered have to be set in the model using the {@link osmo.tester.model.Requirements} object.
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstren
 */
public class RequirementCoverage extends AbstractEndCondition {
  private static Logger log = new Logger(RequirementCoverage.class);
  /** The stopping threshold. */
  private final double threshold;

  /** @param threshold The threshold value, if this is exceeded in evaluation, generation is stopped. */
  public RequirementCoverage(double threshold) {
    if (threshold < 0 || threshold > 1) {
      throw new IllegalArgumentException(RequirementCoverage.class.getSimpleName() + " threshold must be between 0 and 1. Was " + threshold + ".");
    }
    this.threshold = threshold;
  }

  private boolean endNow(TestSuite suite, FSM fsm) {
    Requirements requirements = suite.getRequirements();
    //covered includes "excess" that is the ones that are not registered so we remove those
    double covered = requirements.getUniqueCoverage().size() - requirements.getExcess().size();
    double total = requirements.getRequirements().size();

    double percentage = 0;
    if (total == 0) {
      //always end if no requirements are defined, otherwise it is an infinite loop
      log.debug("No requirements defined, condition is always false.");
      return true;
    } else {
      percentage = covered / total;
    }
    log.debug("threshold:" + threshold + " covered:" + percentage);
    return percentage >= threshold;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return endNow(suite, fsm);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return endNow(suite, fsm);
  }

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public String toString() {
    return "RequirementCoverage{" +
            "threshold=" + threshold +
            '}';
  }
}