package osmo.miner.miner;

import org.junit.Test;
import osmo.miner.log.Logger;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Suite;
import osmo.miner.model.program.Variable;
import osmo.miner.testmodels.TestModels1;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class SuperProgramTests {
  @Test
  public void createFromModel1() {
    Logger.debug = true;
    Suite suite = TestModels1.model1();
    Program superProgram = suite.createEFSM();
    List<Variable> globals = superProgram.getGlobalVariables();
    List<Variable> programVariables = superProgram.getVariables();
    superProgram.getSteps();
  }
}
