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
public class HTML extends CoverageMetric{
  
  public HTML(TestSuite ts) {
    super(ts);
  }

  @Override
  public String getTransitionCoverage() {
    String ret = "<html>\n";
    ret += "<head></head>\n";
    ret += "<body>\n";
    ret += "<table border=\"1\">\n";
    ret += "<tr><td>Name</td><td>Count</td></tr>\n";
    Map<FSMTransition, Integer> coverage = countTransitionCoverage();
     for(Map.Entry<FSMTransition, Integer> a : coverage.entrySet()){
       ret += "<tr><td>"+ a.getKey().getName()+"</td><td>"+a.getValue()+"</td></tr>\n";
     }
     ret += "</table>\n";
     ret += "</body>\n";
     ret += "</html>\n";
     return ret;
  }

  @Override
  public String getTransitionPairCoverage(){
    String ret = "<html>\n";
    ret += "<head></head>\n";
    ret += "<body>\n";
    ret += "<table border=\"1\">\n";
    ret += "<tr><td>From</td><td>To</td><td>Count</td></tr>\n";
    Map<String, Integer> coverage = countTransitionPairCoverage();
    for(Map.Entry<String, Integer> a : coverage.entrySet()){
      String[] b = a.getKey().split(";");
      ret += "<tr><td>"+ b[0]+"</td><td>"+ b[1]+"</td><td>"+a.getValue()+"</td></tr>\n";
    }
    ret += "</table>\n";
    ret += "</body>\n";
    ret += "</html>\n";
    return ret;
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
