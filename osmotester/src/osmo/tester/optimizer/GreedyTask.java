package osmo.tester.optimizer;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A task to be executed to run several instances of {@link GreedyOptimizer} in parallel from a thread pool.
 * Used by {@link MultiGreedy}.
 *
 * @author Teemu Kanstren
 */
public class GreedyTask implements Callable<List<TestCase>> {
  private static Logger log = new Logger(GreedyTask.class);
  /** The optimizer to run in this task. */
  private final GreedyOptimizer optimizer;
  private final long seed;
  private final int populationSize;

  public GreedyTask(GreedyOptimizer optimizer, long seed, int populationSize) {
    this.optimizer = optimizer;
    this.seed = seed;
    this.populationSize = populationSize;
  }

  @Override
  public List<TestCase> call() throws Exception {
    log.debug("Starting task for optimizer:" + optimizer);
    //just run the optimizer..
    List<TestCase> result = optimizer.search(populationSize, seed);
    log.debug("Finished task for optimizer:" + optimizer);
    return result;
  }
}
