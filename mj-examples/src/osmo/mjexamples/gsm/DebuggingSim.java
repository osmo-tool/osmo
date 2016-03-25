package osmo.mjexamples.gsm;

import osmo.common.Logger;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

import java.io.PrintStream;
import java.util.logging.Level;

/** @author Teemu Kanstren */
public class DebuggingSim {
  public static void main(String[] args) {
    OSMOExplorer explorer = new OSMOExplorer();
    GSMModelFactory factory = new GSMModelFactory(System.out);
    //550,4345 = e in test assert, not in explorer..
    for (int i = 1 ; i <= 100 ; i++) {
      long seed = 45345+i*345;
      System.out.println(i+":exploring with seed:"+seed);
      ExplorationConfiguration config = new ExplorationConfiguration(factory, 2, seed);
//      config.setVariableWeight("my-state", 5);
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
    public void createModelObjects(TestModels addHere) {
      SimCard sim = new SimCard(new SimCardAdaptor());
      sim.out = out;
      addHere.add(sim);
    }
  }
}
