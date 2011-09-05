package osmo.miner.miner;

import osmo.miner.model.general.InvariantCollection;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;

/**
 * @author Teemu Kanstren
 */
public interface InvariantMiner {
  public void programStart(Program program);
  public void step(Step step);
  public InvariantCollection getInvariants();
}
