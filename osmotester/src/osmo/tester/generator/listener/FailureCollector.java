package osmo.tester.generator.listener;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.scripter.internal.TestWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Test generation listener that collects failed test cases and writes them to disk when requested.
 * 
 * @author Teemu Kanstren
 */
public class FailureCollector extends AbstractListener {
  /** Set of collected failed tests. */
  private List<TestCase> failed = new ArrayList<>();
  
  @Override
  public void testError(TestCase test, Throwable error) {
    failed.add(test);
  }

  public List<TestCase> getFailed() {
    return failed;
  }

  /**
   * @param toDir The directory, relative to current working directory, where to write the collected traces to.
   */
  public void writeTrace(String toDir) {
    TestWriter writer = new TestWriter(toDir);
    writer.write(failed);
  }
}
