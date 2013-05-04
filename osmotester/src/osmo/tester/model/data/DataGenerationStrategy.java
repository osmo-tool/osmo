package osmo.tester.model.data;

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
  BALANCING,
  /** Loop through all options in an order, restart from beginning after reaching end. */
  ORDERED_LOOP,
  /** Choose values at the option boundaries (below, at, above). Only works for primitives. */
  BOUNDARY_SCAN,
  /** Same as boundary but with invalid values (out of bounds). */
  BOUNDARY_SCAN_INVALID,
  /** Provide random invalid values. */
  RANDOM_INVALID,
  /** Provide invalid values looping through the set of options. */
  ORDERED_LOOP_INVALID,
}
