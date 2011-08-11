package osmo.tester.coverage;

import java.util.HashMap;
import java.util.Map;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * This interface defines the coverage metric used for test generation.
 * 
 * @author Olli-Pekka Puolitaival
 */
public abstract class CoverageMetric {
 
  private TestSuite testSuite = null;
  
  public CoverageMetric(TestSuite ts) {
    this.testSuite = ts;
  }
  
  protected  Map<FSMTransition, Integer> countTransitionCoverage(){
    return testSuite.getTransitionCoverage();
  }
  
  protected Map<String, Integer> countTransitionPairCoverage(){
    Map<String, Integer> ret = new HashMap<String, Integer>();
    
    for(TestCase tc: testSuite.getTestCases()){
      String previous = "Start";
      for(TestStep ts: tc.getSteps()){
        String key = "";
        
        key = previous +";"+ ts.getTransition().getName();
        
        Integer count = ret.get(key);
        if (count == null)
          count = 0;
        ret.put(key, count+1);
          
        previous = ts.getTransition().getName();
      }
    }
    
    return ret;
  }
  
  /**
   * Returns Transition coverage table
   * @return coverage metric in certain kind of format
   */
  public abstract String getTransitionCoverage();
  
  /**
   * 
   * @return
   */
  public abstract String getTransitionPairCoverage();
  
  /**
   * 
   * @return
   */
  public abstract String getRequirementsCoverage();
  
  /**
   * 
   * @return
   */
  public abstract String getTraceabilityMatrix();
}
