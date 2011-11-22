package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * This returns coverage tables in HTML format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class HTML extends CoverageMetric {
  public HTML(TestSuite ts, FSM fsm) {
    super(ts, fsm);
  }

  /*
  public static void main(String[] args) {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("test"));
    suite.endTest();
    System.out.println(new CSV(suite).getTransitionCounts());
    System.out.println(new CSV(suite).getTransitionPairCounts());
  }*/

  public String getTransitionCounts() {
    //note: for this to work, you need to have the IDE or build script copy the .html files to the same location on the output dir (alongside the java classes)
    return super.getTransitionCounts("osmo/tester/reporting/coverage/templates/transition-coverage.txt");
  }

  public String getTransitionPairCounts() {
    return super.getTransitionPairCounts("osmo/tester/reporting/coverage/templates/transitionpair-coverage.txt");
  }

  public String getRequirementCounts() {
    return super.getRequirementsCounts("osmo/tester/reporting/coverage/templates/requirement-coverage.txt");
  }

  @Override
  public String getTraceabilityMatrix() {
    // TODO Auto-generated method stub
    return "Not implemented yet";
  }

}
