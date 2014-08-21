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
  private static final Logger log = new Logger(ExplorationHelper.class);

  public static MainGenerator initPath( ExplorationState state, Collection<String> path) {
    return initPath(state, path, false);
  }
  /**
   * Initializes an exploration path with the current test suite and a new test case up to the given script.
   * This is not always the current concrete generated test case but can also be a location in the explored path.
   * 
   * @param state Current exploration state.
   * @param path The script of the exploration path to be initialized.
   * @param end If true, the test is set as ended after finishing the path.
   * @return The generator initialized with the current test suite and given test path.
   */
  public static MainGenerator initPath(ExplorationState state, Collection<String> path, boolean end) {
    MainGenerator generator = createGenerator(state);
    
    //then we re-create the current test until the current position from which exploration continues
    runScript(generator, path, end);
    return generator;
  }

  /**
   * Runs the given test script to initialize a path for a test case.
   * Before the script is run, the test is initialized according to the OSMO test generation flow.
   * At the end it is finalized similarly if so desired. Typically when re-generating the previous suite
   * we wish to finalize it, whereas when initializing a new test case to explore, we do not wish to finalize it.
   * Finalizing refers to running everything required after a test ends, such as {@link osmo.tester.annotation.AfterTest}
   * methods.
   *
   * @param generator The generator to use.
   * @param path      The sequence of step names forming the path to initialize.
   * @param endTest   If true, the test case is ended through the test suite.
   */
  public static void runScript(MainGenerator generator, Collection<String> path, boolean endTest) {
    generator.beforeTest();
    for (String step : path) {
      execute(generator, step);
    }
    if (endTest) {
      generator.afterTest();
    }
  }

  /**
   * Execute the given test step with the given generator.
   * 
   * @param generator To use for execution.
   * @param step The name of step to execute.
   */
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
   * @param state Current exploration state, including previous suite coverage.
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
    generator.getSuite().setKeepTests(false);
    return generator;
  }

}
