package osmo.tester.model.dataflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static osmo.tester.TestUtils.oneOf;

/**
 * Represents a set of objects of the given type.
 * Input generation picks one of these objects according to the given input strategy.
 * Evaluation checks if the given object is found in the defined set.
 *
 * @author Teemu Kanstren
 */
public class ObjectSet<T> {
  /** The options for data generation and evaluation. */
  private List<T> options = new ArrayList<T>();
  /** The input strategy to choose an object. */
  private GenerationStrategy strategy = GenerationStrategy.RANDOM;
  /** index for next item if using ORDERED_LOOP. Using this instead of iterator to allow modification of options in runtime. */
  private int next = 0;
  /** The history of chosen input value objects for this invariant. */
  private Collection<T> history = new ArrayList<T>();

  public ObjectSet() {
  }

  public ObjectSet(GenerationStrategy strategy) {
    this.strategy = strategy;
  }

  /**
   * Defines the input strategy.
   *
   * @param strategy The new strategy.
   */
  public void setStrategy(GenerationStrategy strategy) {
    this.strategy = strategy;
  }

  /**
   * Adds a new value to the set as potential input and accepted output (evaluation parameter).
   *
   * @param option The object to be added.
   */
  public void addOption(T option) {
    options.add(option);
  }

  /**
   * Removes a value from the set of potential input and accepted output (evaluation parameter).
   * If the object does not exist, nothing is done.
   *
   * @param option The object to be removed.
   */
  public void removeOption(T option) {
    int index = options.indexOf(option);
    if (index <= next) {
      next--;
    }
    options.remove(option);
  }

  /**
   * Evaluates the given object to see if it belongs to this set.
   *
   * @param value The object to be evaluated.
   * @return True if found in the set, false if not.
   */
  public boolean evaluate(T value) {
    return options.contains(value);
  }

  /**
   * Produces an object from this set to be used as input in test cases.
   * The choice depends on the chosen input strategy.
   *
   * @return The chosen input object.
   */
  public T input() {
    List<T> currentOptions = new ArrayList<T>();
    currentOptions.addAll(options);
    if (currentOptions.size() == 0) {
      throw new IllegalStateException("No value to provide (add some options).");
    }
    if (strategy == GenerationStrategy.ORDERED_LOOP) {
      if (next >= currentOptions.size()) {
        next = 0;
      }
      T input = currentOptions.get(next++);
      history.add(input);
      return input;
    }
    if (strategy == GenerationStrategy.OPTIMIZED_RANDOM) {
      currentOptions.removeAll(history);
      if (currentOptions.size() == 0) {
        currentOptions = options;
      }
      //we continue forward but since we removed the covered ones, random will pick the optimized_random now
    }
    //here we default to RANDOM
    T input = oneOf(currentOptions);
    history.add(input);
    return input;
  }

  /**
   * Gives the number of objects in this set.
   *
   * @return The number of objects.
   */
  public int size() {
    return options.size();
  }

  /**
   * Gives all the objects in this set.
   *
   * @return All the objects in this set.
   */
  public Collection<T> getAll() {
    return options;
  }

  @Override
  public String toString() {
    return "ObjectSet{" +
            "strategy=" + strategy +
            ", options=" + options +
            '}';
  }
}
