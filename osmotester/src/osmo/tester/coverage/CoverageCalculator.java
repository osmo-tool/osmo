package osmo.tester.coverage;

import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestStep;

import java.util.Collection;

/**
 * A basis for defining customized coverage criteria for test steps.
 * For example, a pair of values in a test step might be considered important for covering combinations of.
 * Or covering some categories of given values might be considered important, such as 0,1,2+.
 * This is mainly intended to be applied on data variables, but can also be applied to control-flow elements
 * by updating some specific variables through the control-flow.
 * For example, we might want to model a tree structure as input and use a specific transition in the model
 * to start creating a new top-level branch. This transition could update a variable called "branch".
 *
 * @author Teemu Kanstren
 */
public interface CoverageCalculator {
  /**
   * Specific calculators should implement this to produce categories etc.
   *
   * @return The aggregated result.
   */
  public ModelVariable process(TestStep step);

  /**
   * Should provide the set of input variable names this calculator needs to perform the calculations.
   * Used to validate that they exist etc.
   *
   * @return The set of variable names needed as input.
   */
  public Collection<String> getInputNames();
}
