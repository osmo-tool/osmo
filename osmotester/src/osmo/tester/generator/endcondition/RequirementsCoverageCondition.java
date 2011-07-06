package osmo.tester.generator.endcondition;

import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.Requirements;

/**
 * A simple strategy to stop test case generation when all requirements are covered.
 * The requirements to be covered have to be set in the model.
 * 
 * @author Olli-Pekka Puolitaival
 */
public class RequirementsCoverageCondition implements EndCondition {
  private static Logger log = new Logger(LengthCondition.class);
  /** The stopping threshold. */
  private final double threshold;

  /**
   * Constructor.
   *
   * @param threshold The threshold value, if this is exceeded in evaluation, generation is stopped.
   */
  public RequirementsCoverageCondition(double threshold) {
    if (threshold < 0 || threshold > 1) {
      throw new IllegalArgumentException(RequirementsCoverageCondition.class.getSimpleName()+" threshold must be between 0 and 1. Was "+threshold+".");
    }
    this.threshold = threshold;
  }

  @Override
  public boolean endNow(FSM fsm, boolean evaluateSuite) {
    Requirements requirements = fsm.getRequirements();
    //
    double covered = requirements.getCovered().size() - requirements.getExcess().size();
    double total = requirements.getRequirements().size();

    double percentage = 0;
    if(total == 0) {
      //always end if no requirements are defined, otherwise it is an infinite loop
      log.debug("No requirements defined, condition is always false.");
      return true;
    } else {
      percentage = covered/total;
    }
    log.debug("threshold:"+threshold+" covered:"+percentage);
    return percentage >= threshold;
  }
}