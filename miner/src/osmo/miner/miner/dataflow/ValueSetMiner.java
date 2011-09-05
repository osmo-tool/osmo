package osmo.miner.miner.dataflow;

import osmo.miner.miner.InvariantMiner;
import osmo.miner.model.dataflow.ValueRangeInt;
import osmo.miner.model.dataflow.ValueSet;
import osmo.miner.model.general.InvariantCollection;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * What values did the variable have?
 *
 * @author Teemu Kanstren
 */
public class ValueSetMiner implements InvariantMiner {
  private Map<String, ValueSet> globalSets = new HashMap<String, ValueSet>();
  private Map<String, Map<String, ValueSet>> localSets = new HashMap<String, Map<String, ValueSet>>();

  @Override
  public InvariantCollection getInvariants() {
    InvariantCollection invariants = new InvariantCollection();
    invariants.addAll(globalSets.values());
    for (Map<String, ValueSet> locals : localSets.values()) {
      invariants.addAll(locals.values());
    }
    return invariants;
  }

  @Override
  public void programStart(Program program) {
    checkSet(program.getVariables(), globalSets, "global");
    checkLocalSet(program.getName(), program.getVariables());
  }

  @Override
  public void step(Step step) {
    checkSet(step.getVariables(), globalSets, "global");
    checkLocalSet(step.getName(), step.getVariables());
  }

  private void checkLocalSet(String name, Map<String, String> variables) {
    Map<String, ValueSet> sets = localSets.get(name);
    if (sets == null) {
      sets = new HashMap<String, ValueSet>();
      localSets.put(name, sets);
    }
    checkSet(variables, sets, name);
  }

  private void checkSet(Map<String, String> variables, Map<String, ValueSet> sets, String scope) {
    Set<String> names = variables.keySet();
    for (String name : names) {
      ValueSet set = sets.get(name);
      if (set == null) {
        set = new ValueSet(scope, name);
        sets.put(name, set);
      }
      String value = variables.get(name);
      set.add(value);
    }
  }
}
