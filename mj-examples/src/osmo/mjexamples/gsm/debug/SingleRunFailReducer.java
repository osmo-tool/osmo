package osmo.mjexamples.gsm.debug;

import osmo.common.NullPrintStream;
import osmo.common.log.Logger;
import osmo.mjexamples.gsm.GSMModelFactory;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;
import osmo.tester.scripter.internal.TestLoader;
import osmo.tester.scripter.internal.TestScript;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Teemu Kanstren
 */
public class SingleRunFailReducer {
  public static void main(String[] args) {
   // Logger.consoleLevel = Level.FINEST;
    Logger.packageName = "o.t.o.r";
    ReducerConfig config = new ReducerConfig(1);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    TestLoader loader = new TestLoader();
    List<TestScript> scripts = loader.loadTests("demo");
    osmoConfig.setScripts(scripts);
    osmoConfig.setTestEndCondition(new Length(50));
    osmoConfig.setSuiteEndCondition(new Length(1));
    osmoConfig.setFactory(new GSMModelFactory(NullPrintStream.stream));
    config.setIterationTime(TimeUnit.SECONDS, 5);
    config.setTotalTime(TimeUnit.SECONDS, 20);
    config.setPopulationSize(100);
    config.setLength(50);
    reducer.search();
  }
}
