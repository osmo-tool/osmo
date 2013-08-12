package osmo.tester.explorer;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.List;

/**
 * A helper class used to create generators and initialize test suite paths.
 *
 * @author Teemu Kanstren
 */
public class ExplorationHelper {
  private static Logger log = new Logger(ExplorationHelper.class);

  public static MainGenerator initPath( ExplorationState state, Collection<String> script) {
    return initPath(state, script, false);
  }
  /**
   * Initializes an exploration path with the current test suite and a new test case up to the given script.
   * This is not always the current concrete generated test case but can also be a location in the explored path.
   *
   * @param script The script of the exploration path to be initialized.
   * @return The generator initialized with the current test suite and given test path.
   */
  public static MainGenerator initPath(ExplorationState state, Collection<String> script, boolean end) {
    MainGenerator generator = createGenerator(state);
    //then we re-create the current test until the current position from which exploration continues
    runScript(generator, script, end);
    return generator;
  }

  /**
   * Runs the given test script to initialize a path for a test case.
   * Before the script is run, the test is initialized according to the OSMO test generation flow.
   * At the end it is finalized similarly if so desired. Typically when re-generating the previous suite
   * this is not desired, whereas when initializing a new test case, it is desired.
   *
   * @param generator The generator to use.
   * @param script    The script to run.
   * @param endTest   If true, the test case is ended through the test suite.
   */
  public static void runScript(MainGenerator generator, Collection<String> script, boolean endTest) {
    generator.beforeTest();
    for (String step : script) {
      execute(generator, step);
    }
    if (endTest) {
      generator.afterTest();
    }
  }

  public static void execute(MainGenerator generator, String step) {
    List<FSMTransition> enabled = generator.getEnabled();
    FSMTransition transition = null;
    for (FSMTransition option : enabled) {
      if (option.getStringName().equals(step)) {
        transition = option;
        break;
      }
    }
    if (transition == null) {
      throw new NullPointerException("Explorer was unable to find a step with the name '" + step + "'");
    }
    generator.execute(transition);
  }

  /**
   * Creates a generator for exploring a sub-path of the exploration options.
   *
   * @return A new test generator, initialized into exploration mode.
   */
  private static MainGenerator createGenerator(ExplorationState state) {
    ExplorationConfiguration config = state.getConfig();
    TestCoverage suiteCoverage = state.getSuiteCoverage().cloneMe();
    TestSuite suite = new TestSuite(suiteCoverage);

    OSMOConfiguration oc = new OSMOConfiguration();
    config.fillOSMOConfiguration(oc);
    MainGenerator generator = new MainGenerator(state.getConfig().getSeed(), suite, oc);
    generator.invokeAll(generator.getFsm().getExplorationEnablers());
    generator.initSuite();
    return generator;
  }

}
