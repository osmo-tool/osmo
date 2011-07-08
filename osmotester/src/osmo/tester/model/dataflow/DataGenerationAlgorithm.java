package osmo.tester.model.dataflow;

/**
 * Defines a set of strategies for how the different data-flow invariants produce input values when their input() method
 * is invoked.
 *
 * @author Teemu Kanstren
 */
public enum DataGenerationAlgorithm {
  /** Completely random pick. */
  RANDOM,
  /** Pick a value on random but favor previously uncovered partitions or objects. */
  OPTIMIZED_RANDOM,
  /** Loop through all partitions or objects in an order, restart from beginning after reaching end. */
  ORDERED_LOOP
}
