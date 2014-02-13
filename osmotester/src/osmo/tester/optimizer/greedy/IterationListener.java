package osmo.tester.optimizer.greedy;

import osmo.tester.generator.testsuite.TestCase;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public interface IterationListener {
  public void iterationDone(List<TestCase> tests);
  public void generationDone(List<TestCase> tests);
}
