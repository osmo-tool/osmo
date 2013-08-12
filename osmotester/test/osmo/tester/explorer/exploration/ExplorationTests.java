package osmo.tester.explorer.exploration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.explorer.testmodels.CounterFactory;
import osmo.tester.explorer.testmodels.ValueModelFactory;
import osmo.tester.explorer.testmodels.PaperModel1Factory;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.ModelFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ExplorationTests {
  private PaperModel1Factory factory;
  private OSMOExplorer osmo;
  public ExplorationConfiguration config;
  private int seed = 55;

  @Before
  public void setUp() throws Exception {
    factory = new PaperModel1Factory(System.out);
    config = new ExplorationConfiguration(factory, 6, seed);
    osmo = new OSMOExplorer();
    config.setFallbackProbability(1d);
  }

  @Test
  public void calculatorNoFrills() {
    config.setMinSuiteLength(1);
    config.setMinTestLength(1);
    config.setMinTestScore(150);
    osmo.explore(config);
    assertTestCount(1);
    assertSuiteScore(172);
    //172=length+pairs+singles+variablecount+values
    //172=4*0+4*30+2*20+1*10+2
    assertTestSequence(0, "[increase, increase, decrease, increase]");
    TestCoverage coverage = osmo.getSuite().getCoverage();
    assertEquals("Covered pairs", "[.osmo.tester.init->increase, increase->increase, increase->decrease, decrease->increase]", coverage.getStepPairs().toString());
    assertEquals("Covered requirements", "[]", coverage.getRequirements().toString());
    assertEquals("Covered singles", "[increase, decrease]", coverage.getSingles().toString());
    assertEquals("Covered variables", "{counter=[1, 2]}", coverage.getValues().toString());
  }

  /** coverage kriteerit määritellään: x lisäpistettä pitää saada, minimi & maksimipituus (testi ja suite erikseen) */
  @Test
  public void calculatorHighValueScore() {
    config.setMinSuiteLength(1);
    config.setMinTestLength(5);
    config.setStepPairWeight(1);
    config.setStepWeight(1);
    config.setDefaultValueWeight(50);
    config.setFallbackProbability(1d);
    osmo.explore(config);
    assertTestCount(1);
    assertTestSequence(0, "[increase, increase, increase, increase, increase]");
  }

  @Test
  public void maximumLengthCutOffTestCase() {
    config.setMinSuiteLength(1);
    config.setMinTestLength(0);
    config.setMaxTestLength(7);
    config.setMinTestScore(6000);
    config.setStepPairWeight(1);
    config.setStepWeight(1);
    config.setDefaultValueWeight(50);
    osmo.explore(config);
    assertTestCount(1);
    assertSuiteScore(363);
    assertTestSequence(0, "[increase, increase, increase, increase, increase, increase, increase]");
  }

  private void assertTestCount(int expected) {
    TestSuite suite = osmo.getSuite();
    assertEquals("Number of generated tests", expected, suite.getFinishedTestCases().size());
  }

  @Test
  public void maximumLengthCutOffTestSuite() {
    config.setMaxSuiteLength(2);
    config.setMinTestLength(3);
    config.setMinSuiteScore(6000);
    osmo.explore(config);
    assertTestCount(2);
    assertTestSequence(0, "[increase, increase, decrease]");
    assertTestSequence(1, "[increase, increase, increase]");
    assertSuiteScore(143);
  }

  private void assertTestSequence(int testIndex, String sequence) {
    TestCase testCase = osmo.getSuite().getFinishedTestCases().get(testIndex);
    String actual = testCase.getAllStepNames().toString();
    assertEquals("Explored test", sequence, actual);
  }

  private void assertSuiteScore(int expectedScore) {
    TestSuite suite = osmo.getSuite();
    TestCoverage coverage = suite.getCoverage();
    ScoreCalculator scoreCalculator = new ScoreCalculator(config);
    int actualScore = scoreCalculator.calculateScore(coverage);
    assertEquals("Coverage score", expectedScore, actualScore);
  }

  @Test
  @Ignore
  public void minimumScores() {
    config = new ExplorationConfiguration(factory, 10, seed);
    //NOTE: change this to 2000 to get eternally running, plateau hanging stuff
    config.setMinSuiteScore(1000);
    config.setMinTestScore(400);
    config.setMinTestLength(1);
    config.setStepPairWeight(50);
    config.setLengthWeight(0);
    config.setStepWeight(20);
    config.setDefaultValueWeight(50);
//    config.setFallbackProbability(1d);
    osmo.explore(config);
    assertTestCount(2);
    assertSuiteScore(1140);
    assertTestSequence(0, "[start, increase, increase, decrease, increase, increase, increase, increase, increase]");
    assertTestSequence(1, "[start, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase]");
  }

  @Test
  public void plateauTestsZeroMinTestLength() {
//    long start = System.currentTimeMillis();
    config = new ExplorationConfiguration(factory, 9, seed);
    config.setMinSuiteScore(2000);
    config.setMinTestScore(400);
    config.setMinTestLength(0);
    config.setStepPairWeight(50);
    config.setLengthWeight(0);
    config.setStepWeight(20);
    config.setDefaultValueWeight(50);
    config.setTestPlateauThreshold(20);
    config.setSuitePlateauThreshold(20);
    config.setMaxSuiteLength(10);
    config.setFallbackProbability(0.2);
    osmo.explore(config);
    assertSuiteScore(1350);
    assertTestCount(5);
    assertTestSequence(0, "[increase, increase, decrease, increase, increase, increase, increase, increase, increase, increase, increase]");
    assertTestSequence(1, "[increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase]");
    assertTestSequence(2, "[increase, increase, increase, decrease, decrease, increase, increase, decrease, increase, decrease, increase, decrease, increase, decrease, decrease, increase, decrease, increase, decrease]");
    assertTestSequence(3, "[increase, increase, decrease, increase, decrease, increase, decrease, increase, decrease]");
    assertTestSequence(4, "[increase, increase, increase, decrease]");
    long end = System.currentTimeMillis();
//    assertSuiteScore(1400);
//    assertTestCount(4);
//    assertTestSequence(0, "[increase, increase, decrease, increase, increase, increase, increase, increase, increase, increase, increase]");
//    assertTestSequence(1, "[increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, decrease, decrease, increase, increase, decrease, increase, decrease, increase, increase, increase, increase, increase, increase, increase, increase]");
//    assertTestSequence(2, "[increase]");
//    assertTestSequence(3, "[increase, increase, increase, decrease]");
//    long end = System.currentTimeMillis();
//    long diff = end-start;
//    System.out.println("plateau time:"+diff);
  }

  public static void main(String[] args) throws Exception {
    ExplorationTests test = new ExplorationTests();
    test.setUp();
    test.benchmark();
  }

  @Test
  @Ignore
  public void benchmark() {
    long start = System.currentTimeMillis();
    config = new ExplorationConfiguration(factory, 8, seed);
    config.setMinSuiteScore(3000);
    config.setMinTestScore(400);
    config.setMinTestLength(0);
    config.setStepPairWeight(50);
    config.setLengthWeight(10);
    config.setStepWeight(20);
    config.setDefaultValueWeight(50);
    config.setTestPlateauThreshold(20);
    config.setMaxSuiteLength(10);
    osmo.explore(config);
    long end = System.currentTimeMillis();
    long diff = end - start;
    System.out.println("benchmark time:" + diff);
  }
  //aika: 88843 kun ei ole mitään multithreadia
  //aika: 36646 kun optimoitu hieman...
  //47393,44991,42000

//  public static void main(String[] args) {
//    long maxBytes = Runtime.getRuntime().maxMemory();
//    System.out.println("Max memory: " + maxBytes / 1024 / 1024 + "M");
//  }

  @Test
  public void plateauTestsFiveMinTestLength() {
    config = new ExplorationConfiguration(factory, 9, seed);
    config.setMinSuiteScore(2000);
    config.setMinTestScore(400);
    config.setMinTestLength(0);
    config.setStepPairWeight(50);
    config.setLengthWeight(0);
    config.setMinTestLength(5);
    config.setStepWeight(20);
    config.setDefaultValueWeight(50);
    config.setTestPlateauThreshold(20);
    config.setSuitePlateauThreshold(20);
    config.setMaxSuiteLength(10);
    osmo.explore(config);
    assertSuiteScore(1350);
    assertTestCount(5);
    assertTestSequence(0, "[increase, increase, decrease, increase, increase, increase, increase, increase, increase, increase, increase]");
    assertTestSequence(1, "[increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase, increase]");
    assertTestSequence(2, "[increase, increase, increase, decrease, decrease, increase, increase, decrease, increase, decrease, increase, decrease, increase, decrease, decrease, increase, decrease, increase, decrease]");
    assertTestSequence(3, "[increase, increase, decrease, increase, decrease, increase, decrease, increase, decrease]");
    assertTestSequence(4, "[increase, increase, increase, decrease, decrease]");
  }

  @Test
  public void timeOut() {
    config = new ExplorationConfiguration(factory, 8, seed);
    config.setStepWeight(0);
    config.setStepPairWeight(0);
    config.setDefaultValueWeight(0);
    config.setVariableCountWeight(0);
    config.setRequirementWeight(0);
    config.setLengthWeight(1);
    config.setMinTestScore(5000);
    config.setMinSuiteScore(50000);
    config.setTestPlateauThreshold(1);
    config.setTimeout(1);
    long start = System.currentTimeMillis();
    osmo.explore(config);
    long end = System.currentTimeMillis();
    long diff = end - start;
    assertTrue("Timeout should be 1-2s was "+diff+" ms", diff > 1000 && diff < 2000);
  }
  
  @Test
  public void exploredVariablesMatchGenerated() {
    config = new ExplorationConfiguration(new ValueModelFactory(), 8, 8);
    config.setStepWeight(0);
    config.setStepPairWeight(0);
    config.setDefaultValueWeight(1);
    config.setVariableCountWeight(0);
    config.setRequirementWeight(0);
    config.setMinTestLength(5);
    config.setMinSuiteLength(5);
    config.setFallbackProbability(1d);
    osmo.explore(config);
    List<TestCase> tests = osmo.getSuite().getAllTestCases();
    assertEquals("Suite size", 5, tests.size());
    TestCase exploredTest = osmo.getAlgorithm().getExplorationEndCondition().getExploredTest();
    String explored = exploredTest.getStepVariables().toString();
    String generated = tests.get(4).getStepVariables().toString();
    assertEquals("Names variables values", explored, generated);
  }

  @Test
  public void explorationModeEnabled() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    config = new ExplorationConfiguration(new PaperModel1Factory(ps), 8, 8);
    config.setStepWeight(0);
    config.setStepPairWeight(0);
    config.setDefaultValueWeight(1);
    config.setVariableCountWeight(0);
    config.setRequirementWeight(0);
    config.setMinTestLength(5);
    config.setMinSuiteLength(5);
    config.setFallbackProbability(1d);
    osmo.explore(config);
    List<TestCase> tests = osmo.getSuite().getAllTestCases();
    assertEquals("Suite size", 5, tests.size());
    String expected = 
                    "Starting new test case 1\n" +
                    "+++++Starting new test case 2\n" +
                    "+++++Starting new test case 3\n" +
                    "++++-Starting new test case 4\n" +
                    "+++--Starting new test case 5\n" +
                    "+++--";
    String actual = out.toString();
    actual = TestUtils.unifyLineSeparators(actual, "\n");
    assertEquals("Exploration output", expected, actual);
  }
  
  //this resulted in depth >1 -> depth >0 change... which is why it was included
  @Test
  public void counterModelWithMaxLength10() {
    ModelFactory factory = new CounterFactory();
    OSMOExplorer osmo = new OSMOExplorer();
    ExplorationConfiguration config = new ExplorationConfiguration(factory, 4, 55);
    config.setStepWeight(30);
    config.setStepPairWeight(20);
    config.setDefaultValueWeight(7);
    config.setVariableCountWeight(5);
    config.setRequirementWeight(20);
    config.setMaxTestLength(10);
    config.setMinSuiteScore(50);
    config.setMaxSuiteLength(10);
    config.setSuitePlateauThreshold(50);
    osmo.explore(config);
    List<TestCase> cases = osmo.getSuite().getFinishedTestCases();
    String actual = cases.toString();
    String expected = "[TestCase:[start, increase, increase, increase, increase, increase, increase, decrease, decrease, increase], TestCase:[start, increase, decrease, increase, decrease, increase, increase, increase, increase, decrease], TestCase:[start, increase, increase, increase, increase, increase, increase, increase, increase, increase]]";
    assertEquals("Explored counter tests", expected, actual);
  }
}
