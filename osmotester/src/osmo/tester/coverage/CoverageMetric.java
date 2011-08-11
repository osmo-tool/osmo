package osmo.tester.coverage;

import java.util.Collection;
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
  
  protected  Map<FSMTransition, Integer> countTransitions(){
    return testSuite.getTransitionCoverage();
  }
  
  protected Map<String, Integer> countTransitionPairs(){
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
  
  protected Map<String, Integer> countRequirements(){
    Map<String, Integer> ret = new HashMap<String, Integer>();
    
    for(TestCase tc: testSuite.getTestCases()){
      for(TestStep ts: tc.getSteps()){        
        Collection<String> keys = ts.getCoveredRequirements();
        
        for(String key: keys){
          Integer count = ret.get(key);
          if (count == null)
            count = 0;
          ret.put(key, count+1);
        }
      }
    }
    return ret;
  }
  
  /**
   * Returns Transition coverage table
   * @return coverage metric in certain kind of format
   */
  public abstract String getTransitionCounts();
  
  /**
   * 
   * @return
   */
  public abstract String getTransitionPairCounts();
  
  /**
   * 
   * @return
   */
  public abstract String getRequirementsCounts();
  
  /**
   * 
   * @return
   */
  public abstract String getTraceabilityMatrix();
}
