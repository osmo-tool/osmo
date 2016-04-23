package osmo.tester.generator.endcondition;

import osmo.common.Randomizer;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of end conditions from which the generator will pick one based on given weights.
 * Mostly useful for running with the greedy optimizers.
 * Allows the optimizer to build variants of tests at different lengths using one optimizer instance.
 * For example, generate a few very short ones, large number of medium length and some very long ones.
 *
 * @author Teemu Kanstren.
 */
public class WeightedEndConditionSet implements EndCondition {
  /** Weights for end conditions, matched to end conditions by list index. */
  private List<Integer> weights = new ArrayList<>();
  /** The actual end conditions to choose from, matched to weights by index. */
  private List<EndCondition> endConditions = new ArrayList<>();
  /** Currently chosen end condition (e.g., specific to a single test) */
  private EndCondition ec = null;

  /**
   * Creates a set of two weighted end conditions.
   *
   * @param weight1 Weight for the first end condition.
   * @param ec1 The first end condition.
   * @param weight2 Weight for the second end condition.
   * @param ec2 The second end condition.
   */
  public WeightedEndConditionSet(int weight1, EndCondition ec1, int weight2, EndCondition ec2) {
    this.weights.add(weight1);
    this.weights.add(weight2);
    this.endConditions.add(ec1);
    this.endConditions.add(ec2);
  }

  /**
   * Add a new end condition to the set of choices.
   *
   * @param weight Weight for the end condition to add.
   * @param ec The end condition to add.
   */
  public void addEndCondition(int weight, EndCondition ec) {
    this.weights.add(weight);
    this.endConditions.add(ec);
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return ec.endSuite(suite, fsm);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return ec.endTest(suite, fsm);
  }

  /**
   * This initializes the end condition. The generator is expected to call this at the start of each new test.
   * In each case, picks a new end condition according to the given weights and sets this to be used for the test.
   * Makes less sense for suite level, although possible..
   *
   * @param seed Randomization seed.
   * @param fsm Representation of the model.
   * @param config The configuration for the generator session.
   */
  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    //use seed for selection to give deterministic tests
    //note that the generator should pick a new seed for each  test and call this with that seed, resulting in different choices per test
    Randomizer rand = new Randomizer(seed);
    int index = rand.rawWeightedRandomFrom(weights);
    this.ec = endConditions.get(index);
    ec.init(seed, fsm, config);
  }

  @Override
  public EndCondition cloneMe() {
    WeightedEndConditionSet clone = new WeightedEndConditionSet(weights.get(0), endConditions.get(0).cloneMe(), weights.get(1), endConditions.get(1).cloneMe());
    for (int i = 2 ; i < endConditions.size() ; i++) {
      clone.addEndCondition(weights.get(i), endConditions.get(i).cloneMe());
    }
    return clone;
  }
}
