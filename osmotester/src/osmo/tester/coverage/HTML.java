package osmo.tester.coverage;

import java.util.Map;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

/**
 * This returns coverage tables in HTML format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class HTML extends CoverageMetric {
  public HTML(TestSuite ts) {
    super(ts);
  }

  public static void main(String[] args) {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("test"));
    suite.endTest();
    System.out.println(new CSV(suite).getTransitionCounts());
    System.out.println(new CSV(suite).getTransitionPairCounts());
  }

  public String getTransitionCounts() {
    //note: for this to work, you need to have the IDE or build script copy the .html files to the same location on the output dir (alongside the java classes)
    return super.getTransitionCounts("osmo/tester/coverage/templates/transition-coverage.html");
  }

  public String getTransitionPairCounts(){
    return super.getTransitionPairCounts("osmo/tester/coverage/templates/transitionpair-coverage.html");
  }

  @Override
  public String getRequirementsCounts() {
    String ret = "<html>\n";
    ret += "<head></head>\n";
    ret += "<body>\n";
    ret += "<table border=\"1\">\n";
    ret += "<tr><td>Name</td><td>Count</td></tr>\n";
    Map<String, Integer> coverage = countRequirements();
    for(Map.Entry<String, Integer> a : coverage.entrySet()){
      ret += "<tr><td>"+a.getKey()+"</td><td>"+a.getValue()+"</td></tr>\n";
    }
    ret += "</table>\n";
    ret += "</body>\n";
    ret += "</html>\n";
    return ret;
  }

  @Override
  public String getTraceabilityMatrix() {
    // TODO Auto-generated method stub
    return "Not implemented yet";
  }

}
