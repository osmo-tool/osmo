package osmo.tester.model;

/**
 * This represents a factory to create model objects.
 * Used by the versions that run several generators in parallel, such as 
 * {@link osmo.tester.optimizer.GreedyOptimizer},
 * {@link osmo.tester.optimizer.MultiGreedy},
 * {@link osmo.tester.generator.multi.MultiOSMO},
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
