package osmo.miner.miner;

import osmo.miner.model.general.InvariantCollection;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;
import osmo.miner.model.program.Suite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class MainMiner implements InvariantMiner {
  private Collection<InvariantMiner> miners = new ArrayList<InvariantMiner>();

  public void addMiner(InvariantMiner miner) {
    miners.add(miner);
  }

  public void mine(Suite suite) {
    List<Program> programs = suite.getPrograms();
    for (Program program : programs) {
      programStart(program);
      List<Step> steps = program.getSteps();
      for (Step step : steps) {
        step(step);
      }
    }
  }

  @Override
  public void programStart(Program program) {
    for (InvariantMiner miner : miners) {
      miner.programStart(program);
    }
  }

  @Override
  public void step(Step step) {
    Collection<Step> subSteps = step.getSubSteps();
    for (Step subStep : subSteps) {
      step(subStep);
    }
    for (InvariantMiner miner : miners) {
      miner.step(step);
    }
  }

  @Override
  public InvariantCollection getInvariants() {
    InvariantCollection invariants = new InvariantCollection();
    for (InvariantMiner miner : miners) {
      invariants.addAll(miner.getInvariants());
    }
    return invariants;
  }
}
