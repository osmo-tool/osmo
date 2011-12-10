package osmo.miner.testminer.dataflow;

import osmo.miner.testminer.TCInvariantMiner;
import osmo.miner.testminer.model.dataflow.ValueRangeInt;
import osmo.miner.testminer.model.general.InvariantCollection;
import osmo.miner.testminer.testcase.TestCase;
import osmo.miner.testminer.testcase.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO: different numeric types + preconfiguration option to specify type of number
 *
 * @author Teemu Kanstren
 */
public class ValueRangeMiner implements TCInvariantMiner {
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
  public void programStart(TestCase program) {
    checkRange(program.getVariables(), true, true, "global");
    checkRange(program.getVariables(), true, false, program.getName());
  }

  @Override
  public void step(Step step) {
    checkRange(step.getVariables(), false, true, "global");
    checkRange(step.getVariables(), false, false, step.getName());
  }

  private void checkRange(Map<String, String> variables, boolean program, boolean global, String scope) {
    Map<String, ValueRangeInt> ranges = globalRanges;
    if (!global) {
      ranges = localRanges.get(scope);
      if (ranges == null) {
        ranges = new HashMap<String, ValueRangeInt>();
        localRanges.put(scope, ranges);
      }
    }
    Set<String> names = variables.keySet();
    for (String name : names) {
      ValueRangeInt range = ranges.get(name);
      if (range == null) {
        range = new ValueRangeInt(scope, name, program, global);
        ranges.put(name, range);
      }
      if (!range.isValid()) {
        continue;
      }
      String value = variables.get(name);
      range.check(value);
    }
  }
}
