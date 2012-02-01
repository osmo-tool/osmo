package osmo.miner.testminer;

import osmo.miner.testminer.model.general.InvariantCollection;
import osmo.miner.testminer.testcase.TestCase;
import osmo.miner.testminer.testcase.Step;
import osmo.miner.testminer.testcase.Suite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class MainTCMiner implements TCInvariantMiner {
  private Collection<TCInvariantMiner> miners = new ArrayList<TCInvariantMiner>();

  public void addMiner(TCInvariantMiner miner) {
    miners.add(miner);
  }

  public void mine(Suite suite) {
    List<TestCase> programs = suite.getTests();
    for (TestCase program : programs) {
      programStart(program);
      List<Step> steps = program.getSteps();
      for (Step step : steps) {
        step(step);
      }
    }
  }

  @Override
  public void programStart(TestCase program) {
    for (TCInvariantMiner miner : miners) {
      miner.programStart(program);
    }
  }

  @Override
  public void step(Step step) {
    Collection<Step> subSteps = step.getSubSteps();
    for (Step subStep : subSteps) {
      step(subStep);
    }
    for (TCInvariantMiner miner : miners) {
      miner.step(step);
    }
  }

  @Override
  public InvariantCollection getInvariants() {
    InvariantCollection invariants = new InvariantCollection();
    for (TCInvariantMiner miner : miners) {
      invariants.addAll(miner.getInvariants());
    }
    return invariants;
  }
}
