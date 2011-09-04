package osmo.miner.model.program;

import java.util.ArrayList;
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

  public List<Program> getPrograms() {
    List<Program> result = new ArrayList<Program>();
    result.addAll(programs.values());
    return result;
  }
}
