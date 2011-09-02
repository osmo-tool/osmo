package osmo.miner.model.efsm;

import osmo.miner.miner.DataFlowMiner;
import osmo.miner.model.ValuePair;
import osmo.miner.model.program.Step;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Teemu Kanstren
 */
public class TransitionInfo {
  private Collection<ValuePair<Step>> transitions = new ArrayList<ValuePair<Step>>();
  private DataFlowMiner miner = new DataFlowMiner();
  private Collection<Invariant> globalInvariants = new ArrayList<Invariant>();

  public void addTransition(Step from, Step to) {
    ValuePair<Step> steps = new ValuePair<Step>(from, to);
    transitions.add(steps);
  }

  public Collection<Invariant> getGlobalInvariants() {
    Collection<Step> options = new ArrayList<Step>();
    for (ValuePair<Step> pair : transitions) {
      Step source = pair.getValue1();
      options.add(source);
    }
    return miner.mine(options);
  }

  public Collection<String> getTargets() {
    Collection<String> targets = new HashSet<String>();
    for (ValuePair<Step> transition : transitions) {
      targets.add(transition.getValue2().getName());
    }
    return targets;
  }

  public Collection<Invariant> invariantsFor(Step target) {
    Collection<ValuePair<Step>> options = new ArrayList<ValuePair<Step>>();
    for (ValuePair<Step> pair : transitions) {
      Step target2 = pair.getValue2();
      if (target2.equals(target)) {
        options.add(pair);
      }
    }
    return miner.mine(options);
  }
}
