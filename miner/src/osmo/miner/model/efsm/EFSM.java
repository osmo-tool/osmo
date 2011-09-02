package osmo.miner.model.efsm;

import osmo.miner.log.Logger;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class EFSM {
  private static final Logger log = new Logger(EFSM.class);
  private Map<String, TransitionInfo> transitions = new HashMap<String, TransitionInfo>();
  private final Program program = new Program("EFSM");
  public final Step START = new Step(program, "START");

  public void addTransition(Step from, Step to) {
    if (from == null) {
      from = START;
    }
    TransitionInfo ti = transitions.get(from.getName());
    if (ti == null) {
      ti = new TransitionInfo();
      transitions.put(from.getName(), ti);
    }
    ti.addTransition(from.deepCopy(program), to.deepCopy(program));
  }
}
