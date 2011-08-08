package osmo.tester.coverage;

import java.util.Map;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * Returns coverage tables in comma separate value (CSV) format
 * 
 * @author Olli-Pekka Puolitaival
 */
public class CSV implements CoverageMetric {
  
  private TestSuite ts = null;
  
  public CSV(TestSuite ts) {
    this.ts = ts;
  }

  @Override
  public String getTransitionCoverage(){
    String ret = "";
    CoverageCalculator cc = new CoverageCalculator();
    Map<FSMTransition, Integer> coverage = cc.getTransitionCoverage(ts);
     for(Map.Entry<FSMTransition, Integer> a : coverage.entrySet()){
       ret += a.getKey().getName()+";"+a.getValue()+"\n";
     }
     return ret;
  }
  
  public String transitionPairCoverage(){
    //TODO: implement
    /**
     * Output something like this
     * transition1;transition2;2
     * transition2;transition3;0
     * */
    return null;
  }
  
  public String traceabilityMatrix(){
    //TODO: implement
    return null;
  }

  @Override
  public String getTransitionPairCoverage() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getRequirementsCoverage() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTraceabilityMatrix() {
    // TODO Auto-generated method stub
    return null;
  }
}
