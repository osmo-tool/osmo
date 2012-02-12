package osmo.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is similar to {@link TestUtils} but allows one to use specific configuration (random seed).
 *
 * @author Teemu Kanstren
 */
public class Randomizer {
  /**
   * Used for random number generation.
   */
  private Random random = new Random();

  public Randomizer(long seed) {
    setSeed(seed);
  }

  public Randomizer() {
  }

  /**
   * Creates a new Random value generator initialized with the given seed value.
   *
   * @param seed Seed for the new random generator.
   */
  public void setSeed(long seed) {
    random = new Random(seed);
  }

  /**
   * @return A random value.
   */
  public int cInt() {
    return random.nextInt();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random integer between the given bounds, bounds included.
   */
  public int cInt(int min, int max) {
    int diff = max - min + 1;
    int rnd = random.nextInt(diff);
    rnd += min;
    return rnd;
  }

  /**
   * @return A random value.
   */
  public float cFloat() {
    return random.nextFloat();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public float cFloat(float min, float max) {
    float diff = max - min;
    float rnd = random.nextFloat() * diff;
    rnd += min;
    return rnd;
  }

  /**
   * @return A random value.
   */
  public long cLong() {
    return random.nextLong();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public long cLong(long min, long max) {
    long diff = max - min + 1;
    return min + (long) (random.nextDouble() * diff);
  }

  /**
   * @return A random value.
   */
  public byte cByte() {
    return (byte) cInt(Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public byte cByte(byte min, byte max) {
    return (byte) cInt(min, max);
  }

  /**
   * @return A random value.
   */
  public char cChar() {
    return (char) cInt(Character.MIN_VALUE, Character.MAX_VALUE);
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public char cChar(char min, char max) {
    return (char) cInt(min, max);
  }

  /**
   * @return A random value.
   */
  public double cDouble() {
    return random.nextDouble();
  }

  /**
   * @param min Minimum for the generated value.
   * @param max Maximum for the generated value.
   * @return Random value between the given bounds, bounds included.
   */
  public double cDouble(double min, double max) {
    double diff = max - min;
    double rnd = random.nextDouble();
    rnd *= diff;
    rnd += min;
    return rnd;
  }

  /**
   * Gives an index to the list with a weighted probability.
   * For example, with a list of [1,2,2,3], the probability distribution is
   * 0 = 12,5%, 1 = 25%, 2 = 25%, 3 = 37,5%.
   * This is as 0 is the index of 1 in the list, 1 is index of first 2, 2 is index of second 2, and
   * 3 is the index of 3 in the list. The probability distribution is calculated as in 1+2+2+3=8,
   * and [1] (first value in the weight list) = 8/1 = 12,5%. Since the index of the first item is 0, then
   * the value 0 is returned with this probability.
   *
   * @param weights List of weights and their index. LIST HAS TO BE SORTED.
   * @return Random index into the list according to the lists.
   */
  public int rawWeightedRandomFrom(List<Integer> weights) {
    List<Integer> totals = new ArrayList<>();
    int total = 0;
    for (Integer weight : weights) {
      if (weight <= 0) {
        throw new IllegalArgumentException("Weight must be > 0. Was "+weight+".");
      }
      total += weight;
      totals.add(total);
    }
//    System.out.println("weights:"+totals+" total:"+total);
    return sumWeightedRandomFrom(totals);
  }

  /**
   * Same as rawWeightedRandomFrom but allows to skip the total calculation step for higher performance.
   * The input has to be processed in a way rawWeightedRandomFrom would process it. The example from above
   * ([1,2,2,3]) becomes ([1,3,5,8]). That is, the first is added to the second, the result is added to the
   * third, and so on.
   *
   * @param summedTotals The summed weights as described above.
   * @return Random index into the list according to the weights.
   */
  public int sumWeightedRandomFrom(List<Integer> summedTotals) {
    int total = summedTotals.get(summedTotals.size()-1);
    int target = cInt(1, total);
//    System.out.println("target:"+target);
    int choice = Collections.binarySearch(summedTotals, target);
    if (choice < 0) {
      //Java binary search returns negative index values if there is no direct match, with additional -1 added on top
      choice = choice * -1; //make it positive again
      choice = choice -1; //remove the -1 (with after previous line is +1)
    }
    return choice;
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public int oneOf(int[] array) {
    return array[cInt(0, array.length - 1)];
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public <T> T oneOf(T[] array) {
    return array[cInt(0, array.length - 1)];
  }

  /**
   * @param array The list of items where to pick one from.
   * @return A randomly picked item from the given list.
   */
  public <T> T oneOf(Collection<T> array) {
    List<T> list = new ArrayList<>(array);
    return list.get(cInt(0, array.size() - 1));
  }

  /**
   * Picks the minimum number from the given collection. The items in the collection must be types of {@link Number}.
   *
   * @param array The list of items where to pick the value from.
   * @return The minimum numeric value from the given collection.
   */
  public <T extends Number> T minOf(Collection<T> array) {
    T smallest = null;
    for (T t : array) {
      if (smallest == null || t.doubleValue() < smallest.doubleValue()) {
        smallest = t;
      }
    }
    return smallest;
  }

}
