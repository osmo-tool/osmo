package osmo.tester.coverage;

/**
 * This interface defines the coverage metric used for test generation.
 * 
 * @author Olli-Pekka Puolitaival
 */
public interface CoverageMetric {
 
  /**
   * Returns Transition coverage table
   * @return coverage metric in certain kind of format
   */
  public abstract String getTransitionCoverage();
  
  /**
   * 
   * @return
   */
  public abstract String getTransitionPairCoverage();
  
  /**
   * 
   * @return
   */
  public abstract String getRequirementsCoverage();
  
  /**
   * 
   * @return
   */
  public abstract String getTraceabilityMatrix();
}
