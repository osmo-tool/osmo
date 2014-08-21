package osmo.tester.optimizer.greedy;

import osmo.tester.generator.testsuite.TestCase;

import java.util.List;

/**
 * For clients who wish to be notified when an iteration has finished or when a single optimizer has finished in
 * case where multiple are running in parallel.
 * In case of {@link osmo.tester.optimizer.greedy.MultiGreedy}, we are notified or all iterations in all sub-optimizers
 * but only once about generation finish in the end.
 * 
 * @author Teemu Kanstren
 */
public interface IterationListener {
  /**
   * One iteration is done.
   * 
   * @param tests Optimized set of tests so far.
   */
  public void iterationDone(List<TestCase> tests);

  /**
   * Whole optimizer finished.
   * 
   * @param tests Final set of optimized tests.
   */
  public void generationDone(List<TestCase> tests);
}
