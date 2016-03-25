package osmo.mjexamples.gsm.debug;

import osmo.common.NullPrintStream;
import osmo.common.Logger;
import osmo.mjexamples.gsm.GSMModelFactory;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;
import osmo.tester.scripter.internal.TestLoader;
import osmo.tester.scripter.internal.TestScript;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 *
 * @author Teemu Kanstren
 */
public class SingleRunFailReducer {
  public static void main(String[] args) {
    long iterationTime = Long.parseLong(args[0]);
    long totalTime = Long.parseLong(args[1]);
    ReducerConfig config = new ReducerConfig(1);
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    if (args.length > 2) osmoConfig.addListener(new ErrorListener());
    TestLoader loader = new TestLoader();
    List<TestScript> scripts = loader.loadTests("demo");
    osmoConfig.setScripts(scripts);
    osmoConfig.setTestEndCondition(new Length(50));
    osmoConfig.setSuiteEndCondition(new Length(1));
    osmoConfig.setFactory(new GSMModelFactory(NullPrintStream.stream));
    config.setInitialTime(TimeUnit.MINUTES, iterationTime);
    config.setFuzzTime(TimeUnit.MINUTES, iterationTime);
    config.setShorteningTime(TimeUnit.MINUTES, iterationTime);
    config.setPopulationSize(100);
    config.setLength(50);
    reducer.search();
  }

  private static class ErrorListener implements GenerationListener {
    @Override
    public void init(long seed, FSM fsm, OSMOConfiguration config) {

    }

    @Override
    public void guard(FSMTransition transition) {

    }

    @Override
    public void stepStarting(TestCaseStep step) {

    }

    @Override
    public void stepDone(TestCaseStep step) {

    }

    @Override
    public void lastStep(String name) {

    }

    @Override
    public void pre(FSMTransition transition) {

    }

    @Override
    public void post(FSMTransition transition) {

    }

    @Override
    public void testStarted(TestCase test) {

    }

    @Override
    public void testEnded(TestCase test) {

    }

    @Override
    public void testError(TestCase test, Throwable error) {
      error.printStackTrace();
    }

    @Override
    public void suiteStarted(TestSuite suite) {

    }

    @Override
    public void suiteEnded(TestSuite suite) {

    }
  }
}
