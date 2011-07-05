package osmo.tester.generator.strategy;

import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.Requirements;

/**
 * A simple strategy to stop test case generation when all requirements are covered.
 * The requirements to be covered have to be set in the model.
 * 
 * @author Olli-Pekka Puolitaival
 */
public class RequirementsCoverageStrategy implements ExitStrategy {
  private static Logger log = new Logger(LengthStrategy.class);
  /** The stopping threshold. */
  private final double threshold;

  /**
   * Constructor.
   *
   * @param threshold The threshold value, if this is exceeded in evaluation, generation is stopped.
   */
  public RequirementsCoverageStrategy(double threshold) {
    if (threshold < 0 || threshold > 1) {
      throw new IllegalArgumentException(ProbabilityStrategy.class.getSimpleName()+" threshold must be between 0 and 1. Was "+threshold+".");
    }
    this.threshold = threshold;
  }

  @Override
  public boolean exitNow(FSM fsm, boolean evaluateSuite) {
    Requirements requirements = fsm.getRequirements();
    double covered = requirements.getCovered().size() - requirements.getExcess().size();
    double total = requirements.getRequirements().size();

    double percentage = 0;
    if(total == 0) {
      return false;
    } else {
      percentage = covered/total;
    }
    log.debug("threshold:"+threshold+" covered:"+percentage);
    return threshold <= percentage;
  }
}