package osmo.tester;

import osmo.common.TestUtils;
import osmo.common.Logger;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.CSVCoverageReport;
import osmo.tester.reporting.jenkins.JenkinsReportGenerator;
import osmo.tester.reporting.trace.TraceReportWriter;

import java.util.List;
import java.util.logging.Level;

/**
 * The main class for initiating the MBT tool.
 * <p>
 * Create the model object using the annotations from osmo.tester.annotation package.
 * Set test generation stop strategies for both the overall test suite and individual test cases.
 * Set the algorithm for test generation.
 * Invoke generate().
 *
 * @author Teemu Kanstren
 */
public class OSMOTester {
  private static final Logger log = new Logger(OSMOTester.class);
  /** The parsed model for test generation. */
  private FSM fsm = null;
  /** Configuration for test generation. */
  private OSMOConfiguration config = new OSMOConfiguration();
  /** For generating the tests. */
  private MainGenerator generator = null;
  /** Do we print model coverage in the end? */
  private boolean printCoverage = true;

  /** A constructor for use with addModelObject() method. */
  public OSMOTester() {
  }

  public void setPrintCoverage(boolean printCoverage) {
    this.printCoverage = printCoverage;
  }

  public void setModelFactory(ModelFactory factory) {
    config.setFactory(factory);
  }

  /**
   * Adds a new model object, to be composed by OSMO to a single internal model along with other model objects.
   * 
   * When using this method, the object will be used as is, as opposed to the factory which re-creates them always.
   * Note that with optimizers, explorer and multi-osmo the factory is the only supported option.
   *
   * @param modelObject The model object (with OSMO annotations) to be added.
   */
  public void addModelObject(Object modelObject) {
    config.addModelObject(modelObject);
  }  

  /**
   * Adds a model object with a given prefix, allowing the same object class to be re-used with different configuration
   * where the names of test steps, guards and other elements is preceded by the given prefix.
   * 
   * When using this method, the object will be used as is, as opposed to the factory which re-creates them always.
   * Note that with optimizers, explorer and multi-osmo the factory is the only supported option.
   *
   * @param prefix      The model prefix.
   * @param modelObject The model object itself.
   */
  public void addModelObject(String prefix, Object modelObject) {
    config.addModelObject(prefix, modelObject);
  }

  /** Invoke this to perform actual test generation from the given model, with the given algorithms and strategies. 
   * @param seed Generation seed to initialized the test case seeds.*/
  public void generate(long seed) {
    log.d("generator starting up");
    generator = initGenerator(seed);
    generator.generate();
    if (!printCoverage) return;
    TestSuite suite = generator.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    System.out.println("generated " + tests.size() + " tests.\n");
    TestCoverage tc = new TestCoverage(tests);
    String coverage = tc.coverageString(fsm, generator.getPossibleStepPairs(), null, null, null, false);
    System.out.println(coverage);
    Requirements requirements = suite.getRequirements();
    if (!requirements.isEmpty()) {
      System.out.println();
      System.out.println(requirements.printCoverage());
    }
    String filename = "osmo-output/osmo-" + seed;
    if (config.isSequenceTraceRequested()) {
      writeTrace(filename, tests, seed, config);
    }
    if (!config.isExploring()) {
      writeCoverageReport(filename, tests);
    }
  }

  /**
   * This needs to be synchronized or the reports written and system.out can be messed up.
   * 
   * @param filename
   * @param tests
   * @param seed
   * @param config
   */
  public static synchronized void writeTrace(String filename, List<TestCase> tests, long seed, OSMOConfiguration config) {
    createHtmlTrace(filename, tests);
    createJenkinsReport(filename, tests, seed, config);
  }

  private static void writeCoverageReport(String filename, List<TestCase> tests) {
    String summary = "summary\n";
    CSVCoverageReport report = new CSVCoverageReport(new ScoreCalculator(new ScoreConfiguration()));
    report.process(tests);

    String totalCsv = report.report();
    totalCsv += summary + "\n";
    //we only write the coverage report if
    TestUtils.write(totalCsv, filename + ".csv");
  }

  private static void createHtmlTrace(String filename, List<TestCase> tests) {
    TraceReportWriter trace = new TraceReportWriter();
    try {
      trace.write(tests, filename+".html");
    } catch (Exception e) {
      log.e("Failed to write trace", e);
    }
  }

  private static void createJenkinsReport(String filename, List<TestCase> tests, long seed, OSMOConfiguration config) {
    JenkinsReportGenerator jenkins = new JenkinsReportGenerator(null, false);
    jenkins.init(seed, null, config);
    jenkins.suiteStarted(null);
    for (TestCase test : tests) {
      jenkins.testEnded(test);
    }
    jenkins.suiteEnded(null);
    String report = jenkins.generateTestReport();
    TestUtils.write(report, filename + ".xml");
  }

  public MainGenerator initGenerator(long seed) {
    TestSuite suite = new TestSuite();
    if (config.isDataTraceRequested()) suite.enableParameterTracking();
    MainGenerator generator = new MainGenerator(seed, suite, config);
    fsm = generator.getFsm();
//    config.initialize(seed, fsm);
    return generator;
  }

  public TestSuite getSuite() {
    return generator.getSuite();
  }

  public FSM getFsm() {
    if (fsm == null) {
      initGenerator(0);
    }
    return fsm;
  }

  /**
   * Add a condition for stopping the generation of whole test suite.
   *
   * @param condition The new condition to stop overall suite generation.
   */
  public void setSuiteEndCondition(EndCondition condition) {
    config.setSuiteEndCondition(condition);
  }

  /**
   * Add a condition for stopping the generation of individual test cases.
   *
   * @param condition The new condition to stop individual test generation.
   */
  public void setTestEndCondition(EndCondition condition) {
    config.setTestEndCondition(condition);
  }

  /**
   * Set the algorithm for test generation.
   *
   * @param algorithm New test generation algorithm.
   */
  public void setAlgorithm(FSMTraversalAlgorithm algorithm) {
    config.setAlgorithm(algorithm);
  }

  public void addListener(GenerationListener listener) {
    config.addListener(listener);
  }

  public void addFilter(StepFilter filter) {
    config.addFilter(filter);
  }

  public OSMOConfiguration getConfig() {
    return config;
  }

  public void setConfig(OSMOConfiguration config) {
    this.config = config;
  }
}
