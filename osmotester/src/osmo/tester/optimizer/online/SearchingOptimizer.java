package osmo.tester.optimizer.online;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class SearchingOptimizer {
  public void search(OSMOTester tester) {
    Length maxLength = new Length(1000);
    maxLength.setStrict(true);
    tester.addTestEndCondition(maxLength);
    tester.addSuiteEndCondition(maxLength);
  }
}
