package osmo.miner.testminer;

import osmo.miner.testminer.model.general.InvariantCollection;
import osmo.miner.testminer.testcase.TestCase;
import osmo.miner.testminer.testcase.Step;

/**
 * @author Teemu Kanstren
 */
public interface TCInvariantMiner {
  public void programStart(TestCase program);
  public void step(Step step);
  public InvariantCollection getInvariants();
}
