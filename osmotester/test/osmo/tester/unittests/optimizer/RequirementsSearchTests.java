package osmo.tester.unittests.optimizer;

import org.junit.Test;
import osmo.common.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;
import osmo.tester.optimizer.reducer.ReducerState;
import osmo.tester.unittests.testmodels.Model10Factory;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class RequirementsSearchTests {
  @Test
  public void model10find4() {
    Model10Factory factory = new Model10Factory();
    ReducerConfig config = new ReducerConfig(111);
    config.setParallelism(1);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(factory);
    osmoConfig.setTestEndCondition(new Length(50));
    osmoConfig.setSuiteEndCondition(new Length(20));
    //modified 8apr15
//    config.setInitialTime(TimeUnit.SECONDS, 40);
    config.setFuzzTime(TimeUnit.SECONDS, 5);
    config.setShorteningTime(TimeUnit.SECONDS, 5);
    config.setPopulationSize(100);
    config.setLength(50);
    config.setTestMode(true);
    config.setRequirementsTarget(4);
    ReducerState state = reducer.search();
    Map<String,TestCase> tests = state.getRequirementsTests();
    TestCase x3 = tests.get("X3");
    TestCase x10 = tests.get("X10");
    TestCase y10 = tests.get("Y10");
    TestCase xy5 = tests.get("X&Y 5");
    assertEquals("Req X3 length", 3, x3.getLength());
    assertEquals("Req X10 length", 10, x10.getLength());
    assertEquals("Req Y10 length", 11, y10.getLength());
    //epic fail due to too hard to find exact match when some combos pass the value
    assertEquals("Req X&Y5 length", 6, xy5.getLength());
  }

  @Test
  public void model10find2() {
    Model10Factory factory = new Model10Factory();
    ReducerConfig config = new ReducerConfig(111);
    config.setParallelism(1);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(factory);
    config.setFuzzTime(TimeUnit.SECONDS, 5);
    config.setShorteningTime(TimeUnit.SECONDS, 5);
    config.setPopulationSize(100);
    config.setLength(10);
    config.setTestMode(true);
    config.setRequirementsTarget(4);
    ReducerState state = reducer.search();
    Map<String,TestCase> tests = state.getRequirementsTests();
    assertEquals("Number of requirements tests", 2, tests.size());
    TestCase x3 = tests.get("X3");
    assertEquals("Req X3 length", 3, x3.getLength());
    TestCase xy5 = tests.get("X&Y 5");
    assertEquals("Req X&Y 5 length", 6, xy5.getLength());
  }

  @Test
  public void model10find0() {
    Model10Factory factory = new Model10Factory();
    ReducerConfig config = new ReducerConfig(111);
    config.setParallelism(1);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(factory);
    osmoConfig.setTestEndCondition(new Length(50));
    osmoConfig.setSuiteEndCondition(new Length(20));
    config.setInitialTime(TimeUnit.SECONDS, 10);
    config.setFuzzTime(TimeUnit.SECONDS, 10);
    config.setShorteningTime(TimeUnit.SECONDS, 10);
    config.setPopulationSize(500);
    config.setLength(2);
    config.setTestMode(true);
    config.setRequirementsTarget(4);
    ReducerState state = reducer.search();
    Map<String,TestCase> tests = state.getRequirementsTests();
    assertEquals("Number of requirements tests", 0, tests.size());
  }
}
