package osmo.tester.coverage;

import java.util.Map;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * This returns coverage tables in HTML format
 * 
 * @author Olli-Pekka Puolitaival
 *
 */
public class HTML implements CoverageMetric{
  
  private TestSuite ts = null;
  
  public HTML(TestSuite ts) {
    this.ts = ts;
  }

  @Override
  public String getTransitionCoverage() {
    String ret = "<html>\n";
    ret += "<head></head>\n";
    ret += "<body>\n";
    ret += "<table border=\"1\">";
    coverageCalculator cc = new coverageCalculator();
    Map<FSMTransition, Integer> coverage = cc.getTransitionCoverage(ts);
     for(Map.Entry<FSMTransition, Integer> a : coverage.entrySet()){
       ret += "<tr><td>"+ a.getKey().getName()+"</td><td>"+a.getValue()+"</td></tr>\n";
     }
     ret += "</table>\n";
     ret += "</body>\n";
     ret += "</html>\n";
     return ret;
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
