package osmo.tester.coverage;

import java.util.HashMap;
import java.util.Map;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

public class coverageCalculator {
  
  public  Map<FSMTransition, Integer> getTransitionCoverage(TestSuite testsuite){
    Map<FSMTransition, Integer> coverage = new HashMap<FSMTransition, Integer>();
    
    for(TestCase tc: testsuite.getTestCases()){
      for(TestStep ts: tc.getSteps()){
        
        //Calculate transition coverage
        Integer count = coverage.get(ts.getTransition());
        if (count == null) {
          count = 0;
        }
        coverage.put(ts.getTransition(), count+1);
      }
    }
    return coverage;
  }
  
}
