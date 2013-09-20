package osmo.tester.model.data;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.gui.manualdrive.ValueSetGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of values (objects) of the given type.
 * Input generation picks one of these objects according to the used query function.
 *
 * @author Teemu Kanstren
 */
public class ValueSet<T> extends SearchableInput<T> {
  private static final Logger log = new Logger(ValueSet.class);
  /** The options for data generation and evaluation. */
  private List<T> options = new ArrayList<>();
  /** Options that have been booked. These will not be returned by bookXX() methods. */
  private List<T> booked = new ArrayList<>();
  private List<T> unbooked = new ArrayList();
  /** The input strategy to choose an object. */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;
  /** Index for ordered loop. Using this instead of iterator to allow modification of options in runtime. */
  private int next = 0;
  private T choice = null;
  /** The history of chosen values for this ValueSet. */
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
    for (T item : items) {
      add(item);
    }
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
  }

  /**
   * Defines the input strategy.
   *
   * @param strategy The new strategy.
   * @deprecated will remove in next release, use the replacing functions for random() etc.
   */
  @Override
  public ValueSet<T> setStrategy(DataGenerationStrategy strategy) {
    this.strategy = strategy;
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
    unbooked.add(option);
  }

  /**
   * Add all values in the given collection as potential input/output.
   * 
   * @param options The options to add.
   */
  public void addAll(Collection<T> options) {
    this.options.addAll(options);
  }

  /**
   * Adds a new value to the set as potential input and accepted output (evaluation parameter) with a weight.
   * The weight means that the given object will have "weight" number of instances in this set, whereas the
   * ones added with the non-weighted add() method only have one instance. For example, add("teemu");add("bob",6)
   * results in "teemu" being provided once and "bob" six times with looping strategy. With random strategy,
   * "bob" will just have 6 times higher probability to appear. Just as if add("bob") had been called six times.
   * NOTE: this can mess up the booking system as it books only one of these values.
   *
   * @param option The object to be added.
   * @param weight The weight of the option, resulting in this many calls to add().
   */
  public void add(T option, int weight) {
    for (int i = 0 ; i < weight ; i++) {
      options.add(option);
      unbooked.add(option);
    }
  }

  /**
   * Removes a value from the set of potential values.
   * If the object does not exist, nothing is done.
   * If several values exist (e.g., weighted add was done), only first one is removed
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
    unbooked.remove(option);
  }

  /**
   * Produces an object from this set to be used as input in test cases.
   * The choice depends on the chosen input strategy.
   *
   * @return The chosen input object.
   * @deprecated will remove in next release, use the replacing functions for random() etc.
   */
  @Override
  public T next() {
    switch (strategy) {
      case ORDERED_LOOP:
        return ordered();
      case BALANCING:
        return balanced();
      case RANDOM:
        return random();
      default:
        throw new IllegalArgumentException("Unsupported strategy (" + strategy.name() + ") for " + ValueSet.class.getSimpleName());
    }
  }
  
  private void pre() {
    choice = null;
    if (gui != null) {
      choice = (T) gui.next();
      return;
    }
    OSMOConfiguration.check(this);
    if (unbooked.size() == 0) {
      throw new IllegalStateException("No value to provide (add some options).");
    }
  }
  
  private void post() {
    history.add(choice);
    //log.debug("Value:"+next);
    observe(choice);
  }

  /**
   * Randomly picks one of the options.
   * 
   * @return The chosen one.
   */
  public T random() {
    //TODO: refactor all these when moving to JDK8
    pre();
    if (choice == null) {
      choice = rand.oneOf(unbooked);
    }
    post();
    return choice;
  }

  /**
   * Pick an option at random and book it.
   * 
   * @return The chosen one. If all are booked, throws an IllegalStateException.
   */
  public T bookRandom() {
    pre();
    if (choice == null) {
      if (unbooked.isEmpty()) throw new IllegalStateException("Nothing left to book.");
      choice = random();
      booked.add(choice);
      unbooked.remove(choice);
    }
    post();
    return choice;
  }
  
  public T removeRandom() {
    pre();
    if (choice == null) choice = rand.oneOf(options);
    options.remove(choice);
    unbooked.remove(choice);
    booked.remove(choice);
    post();
    return choice;
  }
  
  public void free(T option) {
    boolean ok = booked.remove(option);
    if (!ok) throw new IllegalArgumentException("Given option to free that was not booked:"+option);
  }

  /**
   * Gives a higher probability to less covered options.
   *
   * @return The chosen one.
   */
  public T balanced() {
    pre();
    if (choice == null) {
      Collection<T> choices = new LinkedHashSet<>();
      choices.addAll(unbooked);
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
        int min = rand.minOf(coverage.values());
        for (Map.Entry<T, Integer> item : coverage.entrySet()) {
          if (coverage.get(item.getKey()) == min) {
            choices.add(item.getKey());
          }
        }
      }
      choice = rand.oneOf(choices);
    }
    post();
    return choice;
  }

  /**
   * Treats the choice as an ordered loop through the options.
   *
   * @return The next item according to loop index.
   */
  public T ordered() {
    pre();
    if (choice == null) {
      List<T> currentOptions = new ArrayList<>();
      currentOptions.addAll(unbooked);
      if (next >= currentOptions.size()) {
        next = 0;
      }
      choice = currentOptions.get(next++);
    }
    post();
    return choice;
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
    if (guiEnabled) return;
    guiEnabled = true;
    gui = new ValueSetGUI(this);
  }

  @Override
  public String toString() {
    return "ValueSet{name="+getName()+", " +
            "options=" + options +
            '}';
  }

  /**
   * Sets the set of options to pick from, clearing all history and bookings.
   *
   * @param newOptions The new set of options to use.
   */
  public void setOptions(Collection<T> newOptions) {
    history.clear();
    options.clear();
    unbooked.clear();
    booked.clear();
    options.addAll(newOptions);
    unbooked.addAll(newOptions);
  }

  public void clear() {
    options.clear();
    unbooked.clear();
    booked.clear();
  }
}
