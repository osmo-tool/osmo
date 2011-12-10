package osmo.miner.testminer.dataflow;

import osmo.miner.testminer.TCInvariantMiner;
import osmo.miner.testminer.model.dataflow.ValueSet;
import osmo.miner.testminer.model.general.InvariantCollection;
import osmo.miner.testminer.testcase.TestCase;
import osmo.miner.testminer.testcase.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * What values did the variable have?
 *
 * @author Teemu Kanstren
 */
public class ValueSetMiner implements TCInvariantMiner {
  private Map<String, ValueSet> globalSets = new HashMap<String, ValueSet>();
  private Map<String, Map<String, ValueSet>> localSets = new HashMap<String, Map<String, ValueSet>>();

  @Override
  public InvariantCollection getInvariants() {
    InvariantCollection invariants = new InvariantCollection();
    invariants.addAll(globalSets.values());
    for (Map<String, ValueSet> locals : localSets.values()) {
      invariants.addAll(locals.values());
    }
//    System.out.println("global invariants:" + globalSets);
//    System.out.println("local invariants:" + localSets);
    return invariants;
  }

  @Override
  public void programStart(TestCase program) {
    checkSet(program.getVariables(), true, true, "global");
    checkSet(program.getVariables(), true, false, program.getName());
  }

  @Override
  public void step(Step step) {
    checkSet(step.getVariables(), false, true, "global");
    checkSet(step.getVariables(), false, false, step.getName());
  }

  private void checkSet(Map<String, String> variables, boolean program, boolean global, String scope) {
    Map<String, ValueSet> sets = globalSets;
    if (!global) {
      sets = localSets.get(scope);
      if (sets == null) {
        sets = new HashMap<String, ValueSet>();
        localSets.put(scope, sets);
      }
    }
    Set<String> names = variables.keySet();
    for (String name : names) {
      ValueSet set = sets.get(name);
      if (set == null) {
        set = new ValueSet(scope, name, program, global);
        sets.put(name, set);
      }
      String value = variables.get(name);
      set.add(value);
    }
  }
}
