package osmo.tester.coverage;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * This class provides means to generate coverage metric reports from generated tests.
 * 
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public abstract class CoverageMetric {
  /** The test suite generation history. */
  private TestSuite testSuite = null;
  /** For template->report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();

  public CoverageMetric(TestSuite ts) {
    this.testSuite = ts;
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }
  
  protected  Map<FSMTransition, Integer> countTransitions(){
    return testSuite.getTransitionCoverage();
  }
  
  protected List<TransitionPairCount> countTransitionPairs(){
    Map<TransitionPair, Integer> map = new HashMap<TransitionPair, Integer>();
    
    for(TestCase tc: testSuite.getTestCases()){
      FSMTransition previous = new FSMTransition("Start");
      for(TestStep ts: tc.getSteps()){
        FSMTransition next = ts.getTransition();
        TransitionPair key = new TransitionPair(previous, next);
        Integer count = map.get(key);
        if (count == null) {
          count = 0;
        }
        map.put(key, count+1);
        previous = ts.getTransition();
      }
    }
    List<TransitionPairCount> tpc = new ArrayList<TransitionPairCount>();
    for (Map.Entry<TransitionPair, Integer> entry : map.entrySet()) {
      TransitionPair pair = entry.getKey();
      TransitionPairCount count = new TransitionPairCount(pair, entry.getValue());
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
   * Creates a transition coverage count table, showing how many times each transition
   * has been covered by test cases in test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Transition coverage formatted with given template.
   */
  public String getTransitionCounts(String templateName) {
    Map<FSMTransition, Integer> coverage = countTransitions();
    List<TransitionCount> counts = new ArrayList<TransitionCount>();

    for (Map.Entry<FSMTransition, Integer> a : coverage.entrySet()) {
      TransitionCount count = new TransitionCount(a.getKey(), a.getValue());
      counts.add(count);
    }
    Collections.sort(counts);
    vc.put("transitions", counts);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
    return sw.toString();
  }
  
  /**
   * Creates a transition pair coverage count table, showing how many times each transition pair
   * has been covered by test cases in test suite.
   *
   * @param templateName The name of Velocity template to format the results.
   * @return Transition pair coverage formatted with given template.
   */
  public String getTransitionPairCounts(String templateName) {
    List<TransitionPairCount> tpc = countTransitionPairs();
    Collections.sort(tpc);

    vc.put("pairs", tpc);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(templateName, "UTF8", vc, sw);
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
