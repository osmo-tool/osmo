package osmo.miner.miner;

import org.junit.Test;
import osmo.miner.log.Logger;
import osmo.miner.model.program.Suite;
import osmo.miner.testmodels.TestModels1;

/**
 * @author Teemu Kanstren
 */
public class InvariantModelTests {
  @Test
  public void createFromModel1() {
//    Logger.debug = true;
    Suite suite = TestModels1.model1();
    //InvariantModel superProgram = suite.createEFSM();
    //List<Variable> globals = superProgram.getGlobalVariables();
    //List<Variable> programVariables = superProgram.getVariables();
    //superProgram.getSteps();
  }
}
