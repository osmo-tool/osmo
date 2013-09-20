package osmo.mjexamples.gsm;

import osmo.common.NullPrintStream;
import osmo.common.log.Logger;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.ModelFactory;
import osmo.tester.optimizer.MultiGreedy;
import osmo.tester.parser.ModelObject;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/** @author Teemu Kanstren */
public class DebuggingSim {
  public static void main(String[] args) {
    Logger.consoleLevel = Level.INFO;
    OSMOExplorer explorer = new OSMOExplorer();
    GSMModelFactory factory = new GSMModelFactory(System.out);
    //550,4345 = error in test assert, not in explorer..
    for (int i = 1 ; i <= 100 ; i++) {
      long seed = 45345+i*345; 
      System.out.println(i+":exploring with seed:"+seed);
      ExplorationConfiguration config = new ExplorationConfiguration(factory, 2, seed);
      config.setStateWeight(5);
      config.setStatePairWeight(1);
      config.setStepWeight(30);
      config.setStepPairWeight(20);
      config.setRequirementWeight(10);
      config.setMinTestLength(100);
      config.setMinSuiteLength(20);
      config.setParallelism(4);
      explorer.explore(config);
    }
  }

  private static class GSMModelFactory implements ModelFactory {
    private final PrintStream out;

    private GSMModelFactory(PrintStream out) {
      this.out = out;
    }

    @Override
    public Collection<ModelObject> createModelObjects() {
      Collection<ModelObject> models = new ArrayList<>();
      SimCard sim = new SimCard(new SimCardAdaptor());
      sim.out = out;
      models.add(new ModelObject(sim));
      return models;
    }
  }
}
