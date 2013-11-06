package osmo.tester.explorer.exploration;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.ExplorationState;
import osmo.tester.explorer.MainExplorer;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.explorer.testmodels.CalculatorModelNoPrints;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.testmodels.CalculatorModel;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class BestSearchTests {
  private ExplorationConfiguration config;

  @Before
  public void startUp() {
//    factory = new PaperModelFactory();
    config = new ExplorationConfiguration(null, 3, 111);
    config.setMinSuiteLength(1);
    config.setMaxSuiteLength(10);
    config.setMinTestLength(1);
    config.setMinTestScore(150);
    config.setFallbackProbability(1d);
  }

  @Test
  public void emptySuite4Increase() {
    OSMOTester tester = new OSMOTester();
    tester.setTestEndCondition(new Length(20));
    tester.setSuiteEndCondition(new Length(6));
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    MainGenerator generator = tester.initGenerator(111);
    tester.getConfig().initialize(111, tester.getFsm());
    generator.initSuite();

    long seed = System.currentTimeMillis();
    TestCoverage suiteCoverage = generator.getSuite().getCoverage();

    List<String> script = new ArrayList<>();
    script.add("start");
    script.add("increase");
    script.add("increase");
    script.add("increase");
    script.add("increase");
    MainExplorer explorer = new MainExplorer(null);
    FSM fsm = generator.getFsm();

    ExplorationState state = new ExplorationState(config, suiteCoverage);
    explorer.init(fsm, generator.getSuite(), state, script, 4);

    config.getFallback().init(seed, fsm);

    List<TestCase> tests = new ArrayList<>();
    TestCase test1 = new TestCase(null);
    test1.addStep(new FSMTransition("increase"));
    test1.addStep(new FSMTransition("increase"));
    test1.addStep(new FSMTransition("increase"));
    test1.addStep(new FSMTransition("increase"));
    test1.addStep(new FSMTransition("increase"));
    tests.add(test1);

    TestCase test2 = new TestCase(null);
    test2.addStep(new FSMTransition("increase"));
    test2.addStep(new FSMTransition("increase"));
    test2.addStep(new FSMTransition("increase"));
    test2.addStep(new FSMTransition("increase"));
    test1.addStep(new FSMTransition("decrease"));
    tests.add(test2);

    String best = explorer.findBestFrom(tests, 0);
    assertEquals("decrease", best);
  }

  @Test
  public void timeToFindBest() {
    OSMOTester tester = new OSMOTester();
    tester.setTestEndCondition(new Length(20));
    tester.setSuiteEndCondition(new Length(6));
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModelNoPrints.class));
    MainGenerator generator = tester.initGenerator(111);
    tester.getConfig().initialize(111, tester.getFsm());
    generator.initSuite();

    long start = System.currentTimeMillis();
    int seed = 55;
    TestCoverage suiteCoverage = generator.getSuite().getCoverage();

    List<String> script = new ArrayList<>();
    script.add("start");
    script.add("increase");
    script.add("increase");
    script.add("increase");
    script.add("increase");
    ExplorationState state = new ExplorationState(config, suiteCoverage);

    MainExplorer explorer = new MainExplorer(null);
    FSM fsm = generator.getFsm();
    explorer.init(fsm, generator.getSuite(), state, script, 4);
    config.getFallback().init(seed, fsm);
    
    List<TestCase> tests = new ArrayList<>();
    for (int i = 0 ; i < 50 ; i++) {
      tests.add(generator.nextTest());
    }
    String best = explorer.findBestFrom(tests, 0);
    long end = System.currentTimeMillis();
    long diff = end - start;
    assertTrue("Time to find best from small set should be <500, was "+diff, diff < 500);
  }
  //5067
}
