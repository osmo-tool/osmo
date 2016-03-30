package osmo.mjexamples.gsm.debug;

import osmo.common.NullPrintStream;
import osmo.common.Logger;
import osmo.mjexamples.gsm.GSMModelFactory;
import osmo.tester.OSMOConfiguration;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;
import osmo.tester.optimizer.reducer.ReducerState;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Teemu Kanstren
 */
public class ReducerMain {
  public static void main(String[] args) {
    long seed = Long.parseLong(args[0]);
    System.out.println("Using seed:"+seed);
    ReducerConfig config = new ReducerConfig(seed);
    config.setPrintExplorationErrors(true);
//    config.setParallelism(1);
    Reducer reducer = new Reducer(config);
    reducer.setDeleteOldOutput(false);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(new GSMModelFactory(NullPrintStream.stream));
    config.setInitialTime(TimeUnit.MINUTES, 1);
    config.setFuzzTime(TimeUnit.MINUTES, 1);
    config.setShorteningTime(TimeUnit.MINUTES, 1);
    config.setPopulationSize(100);
    config.setLength(50);
    ReducerState state = reducer.search();

  }
}
