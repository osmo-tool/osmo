package osmo.tester.model.data;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.gui.manualdrive.ValueSetGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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
  /** Options that have been reserved. These will not be returned by reserveXX() methods. */
  private List<T> reserved = new ArrayList<>();
  /** Options still available for reserving. */
  private List<T> free = new ArrayList<>();
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

  private void init() {
  }

  /**
   * Adds a new value to the set as potential input and accepted output (evaluation parameter).
   *
   * @param option The object to be added.
   */
  public void add(T option) {
    options.add(option);
    free.add(option);
  }

  /**
   * Add all values in the given collection as potential input/output.
   * 
   * @param options The options to add.
   */
  public void addAll(Collection<T> options) {
    this.options.addAll(options);
    free.addAll(options);
  }

  /**
   * Adds a new value to the set as potential input and accepted output (evaluation parameter) with a weight.
   * The weight means that the given object will have "weight" number of instances in this set, whereas the
   * ones added with the non-weighted add() method only have one instance. For example, add("teemu");add("bob",6)
   * results in "teemu" being provided once and "bob" six times with looping strategy. With random strategy,
   * "bob" will just have 6 times higher probability to appear. Just as if add("bob") had been called six times.
   * NOTE: this can mess up the reservation system as it reserves only one of these values.
   *
   * @param option The object to be added.
   * @param weight The weight of the option, resulting in this many calls to add().
   */
  public void add(T option, int weight) {
    for (int i = 0 ; i < weight ; i++) {
      options.add(option);
      free.add(option);
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
    free.remove(option);
    reserved.remove(option);
  }
  
  private void pre() {
    if (rand == null) throw new IllegalStateException("You need to set seed before using data objects");
    choice = null;
    if (gui != null) {
      choice = (T) gui.next();
      return;
    }
    OSMOConfiguration.check(this);
    if (options.isEmpty()) {
      throw new IllegalStateException("No value to provide (add some options).");
    }
  }
  
  private void post() {
    history.add(choice);
    //log.debug("Value:"+next);
    record(choice);
  }

  /**
   * Randomly picks one of the free options.
   * 
   * @return The chosen one.
   */
  public T random() {
    pre();
    if (choice == null) {
      if (free.isEmpty()) throw new IllegalStateException("No free to choose from.");
      choice = rand.oneOf(free);
    }
    post();
    return choice;
  }

  /**
   * Randomly picks one of the options regardless of reserved status.
   *
   * @return The chosen one.
   */
  public T randomAny() {
    pre();
    if (choice == null) {
      choice = rand.oneOf(options);
    }
    post();
    return choice;
  }

  /**
   * Randomly picks one of the reserved ones.
   * 
   * @return The chosen one.
   */
  public T randomReserved() {
    pre();
    if (choice == null) {
      if (reserved.isEmpty()) throw new IllegalStateException("No reserved to choose from.");
      choice = rand.oneOf(reserved);
    }
    post();
    return choice;
  }

  /**
   * Reserves the given option. TODO: tests for all uncovered.
   * 
   * @param t Option to reserve.
   */
  public void reserve(T t) {
    if (!options.contains(t)) throw new IllegalArgumentException("Tried to reserve non-existing option:"+t);
    if (!free.contains(t)) throw new IllegalArgumentException("Tried to reserve something that is not free:"+t);
    reserved.add(t);
    free.remove(t);
  }

  /**
   * Gives the number of items reserved currently in this ValueSet.
   * 
   * @return The number of reserved items.
   */
  public int reserved() {
    return reserved.size();
  }
  
  /**
   * Pick an option at random and reserves it.
   * 
   * @return The chosen one. If all are reserved, throws an IllegalStateException.
   */
  public T reserve() {
    pre();
    if (choice == null) {
      if (free.isEmpty()) throw new IllegalStateException("Nothing left to reserve.");
      choice = rand.oneOf(free);
      reserved.add(choice);
      free.remove(choice);
    }
    post();
    return choice;
  }

  /**
   * Picks an option from all options (ignoring reserved status) and removes it.
   * 
   * @return The removed option.
   */
  public T removeRandom() {
    pre();
    if (choice == null) choice = rand.oneOf(options);
    options.remove(choice);
    free.remove(choice);
    reserved.remove(choice);
    post();
    return choice;
  }

  /**
   * Makes a previously reserved option available again.
   * 
   * @param option The option to free.
   */
  public void free(T option) {
    boolean ok = reserved.remove(option);
    if (!ok) throw new IllegalArgumentException("Given option to free that was not reserved:"+option);
    free.add(option);
  }

  /**
   * Gives the number of available options.
   * 
   * @return Number of available options.
   */
  public int available() {
    return free.size();
  }

  /**
   * Checks if the given option is part of this set (reserved or not).
   * 
   * @param option To check.
   * @return True if found.
   */
  public boolean contains(T option) {
    return options.contains(option);
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
      choices.addAll(free);
      choices.removeAll(history);
      //choices now has all items that have never been covered

      if (choices.size() == 0) {
        Map<T, Integer> coverage = new LinkedHashMap<>();
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
  public T loop() {
    pre();
    if (choice == null) {
      List<T> currentOptions = new ArrayList<>();
      currentOptions.addAll(free);
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

  public List<T> getFreeOptions() {
    return free;
  }
  
  public List<T> getReservedOptions() {
    return reserved;
  }

  @Override
  public void enableGUI() {
    if (gui != null) return;
    gui = new ValueSetGUI(this);
  }

  @Override
  public String toString() {
    return "ValueSet{name="+getName()+", " +
            "options=" + options +
            '}';
  }

  /**
   * Sets the set of options to pick from, clearing all history and reservations.
   *
   * @param newOptions The new set of options to use.
   */
  public void setOptions(Collection<T> newOptions) {
    history.clear();
    options.clear();
    free.clear();
    reserved.clear();
    options.addAll(newOptions);
    free.addAll(newOptions);
  }

  public void clear() {
    options.clear();
    free.clear();
    reserved.clear();
  }

  public long getSeed() {
    return rand.getSeed();
  }
}
