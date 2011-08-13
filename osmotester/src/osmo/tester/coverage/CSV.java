package osmo.tester.coverage;

import java.util.Map;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

/**
 * Returns coverage tables in comma separate value (CSV) format
 * 
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class CSV extends CoverageMetric {
  public CSV(TestSuite ts, FSM fsm) {
    super(ts, fsm);
  }

  public String getTransitionCounts(){
    //note: for this to work, you need to have the IDE or build script copy the .csv files to the same location on the output dir (alongside the java classes)
    return super.getTransitionCounts("osmo/tester/coverage/templates/transition-coverage.csv");
  }
  
  /**
   * Output something like this
   * transition1;transition2;2
   * transition2;transition3;0
   * */
  public String getTransitionPairCounts(){
    return super.getTransitionPairCounts("osmo/tester/coverage/templates/transitionpair-coverage.csv");
  }
  
  public String traceabilityMatrix(){
    //TODO: implement
    return null;
  }

  public String getRequirementCounts() {
    return super.getRequirementsCounts("osmo/tester/coverage/templates/requirement-coverage.csv");
  }

  @Override
  public String getTraceabilityMatrix() {
    // TODO Auto-generated method stub
    return "Not implemented yet";
  }
}
