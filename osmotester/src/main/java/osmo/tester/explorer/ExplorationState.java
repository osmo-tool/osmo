package osmo.tester.explorer;

import osmo.tester.coverage.TestCoverage;

/**
 * Holds the overall state for the OSMO Explorer.
 *
 * @author Teemu Kanstren
 */
public class ExplorationState {
  /** Configuration to use. */
  private final ExplorationConfiguration config;
  /** The coverage of the current test suite. Used to evaluate added coverage of the paths. */
  private final TestCoverage suiteCoverage;

  public ExplorationState(ExplorationConfiguration config, TestCoverage suiteCoverage) {
    this.config = config;
    this.suiteCoverage = suiteCoverage;
  }

  public ExplorationConfiguration getConfig() {
    return config;
  }

  public TestCoverage getSuiteCoverage() {
    return suiteCoverage;
  }
}
