package osmo.miner.model.program;

import osmo.miner.model.efsm.EFSM;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Suite {
  private Map<String, Program> programs = new HashMap<String, Program>();

  public void add(Program program) {
    programs.put(program.getName(), program);
  }

  public Collection<Program> getPrograms() {
    return programs.values();
  }

  public EFSM createEFSM() {
    EFSM efsm = new EFSM();
    for (Program program : programs.values()) {
      List<Step> steps = program.getSteps();
      Step previous = null;
      for (Step step : steps) {
        efsm.addTransition(previous, step);
      }
    }
    return efsm;
  }
}
