package osmo.tester.parser;

import osmo.tester.model.FSM;
import osmo.tester.model.Requirements;

/**
 * Represents the results of parsing the given model object(s).
 *
 * @author Teemu Kanstren
 */
public class ParserResult {
  /** The basic structure of the model. */
  private final FSM fsm;
  /** Requirements object for the model. */
  private Requirements requirements = null;

  public ParserResult(FSM fsm) {
    this.fsm = fsm;
  }

  public FSM getFsm() {
    return fsm;
  }

  public Requirements getRequirements() {
    return requirements;
  }

  public void setRequirements(Requirements requirements) {
    this.requirements = requirements;
  }
}
