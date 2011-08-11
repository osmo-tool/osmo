package osmo.tester.coverage;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * This interface defines the coverage metric used for test generation.
 * 
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public abstract class CoverageMetric {
  private TestSuite testSuite = null;
  private VelocityEngine velocity = new VelocityEngine();
  private VelocityContext vc = new VelocityContext();

  public CoverageMetric(TestSuite ts) {
    this.testSuite = ts;
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }
  
  protected  Map<FSMTransition, Integer> countTransitions(){
    return testSuite.getTransitionCoverage();
  }
  
  protected Collection<TransitionPairCount> countTransitionPairs(){
    Map<FSMTransition[], Integer> map = new HashMap<FSMTransition[], Integer>();
    
    for(TestCase tc: testSuite.getTestCases()){
      FSMTransition previous = new FSMTransition("Start");
      for(TestStep ts: tc.getSteps()){
        FSMTransition next = ts.getTransition();
        FSMTransition[] key = new FSMTransition[2];
        key[0] = previous;
        key[1] = next;
        Integer count = map.get(key);
        if (count == null) {
          count = 0;
        }
        map.put(key, count+1);
        previous = ts.getTransition();
      }
    }
    Collection<TransitionPairCount> tpc = new ArrayList<TransitionPairCount>();
    for (Map.Entry<FSMTransition[], Integer> entry : map.entrySet()) {
      FSMTransition[] pair = entry.getKey();
      TransitionPairCount count = new TransitionPairCount(pair[0], pair[1], entry.getValue());
      tpc.add(count);
    }
    return tpc;
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
  public String getTransitionCounts(String templateName) {
    Map<FSMTransition, Integer> coverage = countTransitions();
    Collection<TransitionCount> tc = new ArrayList<TransitionCount>();

    for (Map.Entry<FSMTransition, Integer> a : coverage.entrySet()) {
      tc.add(new TransitionCount(a.getKey(), a.getValue()));
    }
    vc.put("transitions", tc);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
//    velocity.mergeTemplate("osmo/tester/coverage/templates/transition-coverage.html", "UTF8", vc, sw);
    return sw.toString();
  }
  
  /**
   * 
   * @return
   */
  public String getTransitionPairCounts(String templateName) {
    Collection<TransitionPairCount> tpc = countTransitionPairs();

    vc.put("pairs", tpc);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
//    velocity.mergeTemplate("osmo/tester/coverage/templates/transition-coverage.html", "UTF8", vc, sw);
    return sw.toString();
  }
  
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
