package osmo.mjexamples.gsm.debug;

import osmo.common.NullPrintStream;
import osmo.common.log.Logger;
import osmo.mjexamples.gsm.GSMModelFactory;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.optimizer.reducer.Reducer;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Teemu Kanstren
 */
public class ReducerMain {
  public static void main(String[] args) {
    Logger.consoleLevel = Level.INFO;
    Reducer reducer = new Reducer(111);
    OSMOConfiguration config = reducer.getConfig();
    config.setFactory(new GSMModelFactory(NullPrintStream.stream));
    config.setTestEndCondition(new Length(50));
    config.setSuiteEndCondition(new Length(20));
    reducer.search(4, TimeUnit.HOURS, 500, 50);
  }
}
