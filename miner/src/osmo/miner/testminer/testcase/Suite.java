package osmo.miner.testminer.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Suite {
  private Map<String, TestCase> tests = new HashMap<String, TestCase>();

  public void add(TestCase test) {
    tests.put(test.getName(), test);
  }

  public List<TestCase> getTests() {
    List<TestCase> result = new ArrayList<TestCase>();
    result.addAll(tests.values());
    return result;
  }
}
