package osmo.miner.miner.dataflow;

import osmo.miner.miner.InvariantMiner;
import osmo.miner.model.dataflow.InvariantCollection;
import osmo.miner.model.dataflow.ValueRangeInt;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO: different numeric types + preconfiguration option to specify type of number
 *
 * @author Teemu Kanstren
 */
public class ValueRangeMiner implements InvariantMiner {
  private Map<String, ValueRangeInt> globalRanges = new HashMap<String, ValueRangeInt>();
  private Map<String, Map<String, ValueRangeInt>> localRanges = new HashMap<String, Map<String, ValueRangeInt>>();

  @Override
  public InvariantCollection getInvariants() {
    InvariantCollection invariants = new InvariantCollection();
    invariants.addAll(globalRanges.values());
    for (Map<String, ValueRangeInt> locals : localRanges.values()) {
      invariants.addAll(locals.values());
    }
    return invariants;
  }

  @Override
  public void programStart(Program program) {
    checkRange(program.getVariables(), globalRanges, "global");
    checkLocalRange(program.getName(), program.getVariables());
  }

  @Override
  public void step(Step step) {
    checkRange(step.getVariables(), globalRanges, "global");
    checkLocalRange(step.getName(), step.getVariables());
  }

  private void checkLocalRange(String name, Map<String, String> variables) {
    Map<String, ValueRangeInt> ranges = localRanges.get(name);
    if (ranges == null) {
      ranges = new HashMap<String, ValueRangeInt>();
      localRanges.put(name, ranges);
    }
    checkRange(variables, ranges, name);
  }

  private void checkRange(Map<String, String> variables, Map<String, ValueRangeInt> ranges, String scope) {
    Set<String> names = variables.keySet();
    for (String name : names) {
      ValueRangeInt range = ranges.get(name);
      if (range == null) {
        range = new ValueRangeInt(scope, name);
        ranges.put(name, range);
      }
      if (!range.isValid()) {
        return;
      }
      String value = variables.get(name);
      range.check(value);
    }
  }
}
