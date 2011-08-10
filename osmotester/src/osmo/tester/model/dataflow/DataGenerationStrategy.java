package osmo.tester.model.dataflow;

/**
 * Defines a set of strategies for how the different data-flow modeling objects produce input values when their input
 * method (nextXX) is invoked.
 *
 * @author Teemu Kanstren
 */
public enum DataGenerationStrategy {
  /** Completely random pick. */
  RANDOM,
  /** Pick a value on random but favor previously uncovered partitions or objects. */
  OPTIMIZED_RANDOM,
  /** Loop through all options in an order, restart from beginning after reaching end. */
  ORDERED_LOOP,
  /** Choose values at the option boundaries (below, at, above). Only works for primitives. */
  BOUNDARY_SCAN
}
