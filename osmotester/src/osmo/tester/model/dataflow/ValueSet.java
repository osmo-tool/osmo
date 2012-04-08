package osmo.tester.model.dataflow;

import osmo.common.log.Logger;
import osmo.tester.gui.manualdrive.ValueSetGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static osmo.common.TestUtils.minOf;
import static osmo.common.TestUtils.oneOf;

/**
 * Represents a set of values (objects) of the given type.
 * Input generation picks one of these objects according to the given input strategy.
 * Evaluation checks if the given object is found in the defined set.
 * Defaults to random strategy.
 *
 * @author Teemu Kanstren
 */
public class ValueSet<T> extends SearchableInput<T> {
  private static final Logger log = new Logger(ValueSet.class);
  /** The options for data generation and evaluation. */
  private List<T> options = new ArrayList<>();
  /** The input strategy to choose an object. */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;
  /** Index for next item if using ORDERED_LOOP. Using this instead of iterator to allow modification of options in runtime. */
  private int next = 0;
  /** The history of chosen input value objects for this invariant. */
  private Collection<T> history = new ArrayList<>();

  /** Constructor for when no initial options are provided. Options need to be added later with addOption(). */
  public ValueSet() {
    init();
  }

  /**
   * Constructor for when an initial set of options is provided. New ones can still be added later.
   *
   * @param items The initial set of items.
   */
  @SafeVarargs
  public ValueSet(T... items) {
    Collections.addAll(options, items);
    init();
  }

  /**
   * Constructor for defining the initial input generation strategy.
   *
   * @param strategy The algorithm for data generation.
   */
  public ValueSet(DataGenerationStrategy strategy) {
    this.strategy = strategy;
    init();
  }

  private void init() {
    allSupported = true;
  }

  /**
   * Defines the input strategy.
   *
   * @param strategy The new strategy.
   */
  @Override
  public ValueSet<T> setStrategy(DataGenerationStrategy strategy) {
    if (this.strategy != DataGenerationStrategy.SCRIPTED) {
      this.strategy = strategy;
    }
    return this;
    //otherwise we just ignore it since we are running in scripted mode
  }

  /**
   * Adds a new value to the set as potential input and accepted output (evaluation parameter).
   *
   * @param option The object to be added.
   */
  public void add(T option) {
    options.add(option);
  }

  /**
   * Removes a value from the set of potential input and accepted output (evaluation parameter).
   * If the object does not exist, nothing is done.
   *
   * @param option The object to be removed.
   */
  public void remove(T option) {
    int index = options.indexOf(option);
    if (index < 0) {
      return;
    }
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

  @Override
  public boolean evaluateSerialized(String item) {
    for (T option : options) {
      if (option.toString().equals(item)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Produces an object from this set to be used as input in test cases.
   * The choice depends on the chosen input strategy.
   *
   * @return The chosen input object.
   */
  @Override
  public T next() {
    if (options.size() == 0) {
      throw new IllegalStateException("No value to provide (add some options).");
    }
    if (gui != null) {
      return (T) gui.next();
    }
    T next = null;
    switch (strategy) {
      case ORDERED_LOOP:
        next = orderedLoopChoice();
        break;
      case BALANCING:
        next = optimizedRandomChoice();
        break;
      case RANDOM:
        next = oneOf(options);
        break;
      case SCRIPTED:
        next = scriptedNext(scriptNextSerialized());
        break;
      default:
        throw new IllegalArgumentException("Unsupported strategy (" + strategy.name() + ") for " + ValueSet.class.getSimpleName());
    }
    history.add(next);
    //log.debug("Value:"+next);
    observe(next);
    return next;
  }

  private T scriptedNext(String serialized) {
    for (T option : options) {
      if (option.toString().equals(serialized)) {
        return option;
      }
    }
    throw new IllegalArgumentException("Requested scripted value for variable '" + getName() + "' not found: " + serialized);
  }

  /**
   * Implements the optimized random strategy.
   *
   * @return The next item according to this strategy.
   */
  private T optimizedRandomChoice() {
    Collection<T> choices = new HashSet<>();
    choices.addAll(options);
    choices.removeAll(history);
    //choices now has all items that have never been covered

    if (choices.size() == 0) {
      Map<T, Integer> coverage = new HashMap<>();
      for (T t : history) {
        Integer count = coverage.get(t);
        //when first encountered, the object will have "null" instances so we translate that to 0
        //if an object is in history multiple times, the following ones will just increment the value
        if (count == null) {
          count = 0;
        }
        count++;
        coverage.put(t, count);
      }
      int min = minOf(coverage.values());
      for (Map.Entry<T, Integer> item : coverage.entrySet()) {
        if (coverage.get(item.getKey()) == min) {
          choices.add(item.getKey());
        }
      }
    }
    return oneOf(choices);
  }

  /**
   * Implements the ordered loop strategy.
   *
   * @return The next item according to this strategy.
   */
  private T orderedLoopChoice() {
    List<T> currentOptions = new ArrayList<>();
    currentOptions.addAll(options);
    if (next >= currentOptions.size()) {
      next = 0;
    }
    T input = currentOptions.get(next++);
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
  public List<T> getOptions() {
    return options;
  }

  @Override
  public void enableGUI() {
    gui = new ValueSetGUI(this);
  }

  @Override
  public String toString() {
    return "ValueSet{" +
            "options=" + options +
            '}';
  }
}
