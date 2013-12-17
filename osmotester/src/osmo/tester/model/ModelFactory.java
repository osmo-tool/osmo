package osmo.tester.model;

/**
 * This represents a factory to create model objects.
 * Used by the versions that run several generators in parallel, such as 
 * {@link osmo.tester.optimizer.greedy.GreedyOptimizer},
 * {@link osmo.tester.optimizer.greedy.MultiGreedy},
 * {@link osmo.tester.optimizer.multi.MultiOSMO},
 * {@link osmo.tester.explorer.OSMOExplorer}.
 * 
 * @author Teemu Kanstren 
 */
public interface ModelFactory {
  /**
   * This should create the set of model objects and pass them to the generator through the given reference.
   * 
   * @param  addThemHere Add the model objects here.
   */
  public void createModelObjects(TestModels addThemHere);
}
