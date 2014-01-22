package osmo.mjexamples.gsm.debug;

import osmo.common.NullPrintStream;
import osmo.common.log.Logger;
import osmo.mjexamples.gsm.GSMModelFactory;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Teemu Kanstren
 */
public class ReducerMain {
  public static void main(String[] args) {
    Logger.consoleLevel = Level.INFO;
    Reducer reducer = new Reducer(1);
    reducer.setDeleteOldOutput(true);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(new GSMModelFactory(NullPrintStream.stream));
    osmoConfig.setTestEndCondition(new Length(50));
    osmoConfig.setSuiteEndCondition(new Length(20));
    ReducerConfig config = new ReducerConfig(111); 
    config.setIterationTime(TimeUnit.MINUTES, 1);
    config.setPopulationSize(100);
    config.setLength(50);
    reducer.search(config);
  }
}
