package osmo.tester.model.dataflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static osmo.tester.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class ValueRange {
  private enum DataType {INT, LONG, DOUBLE};
  private double min = Double.MIN_VALUE;
  private double max = Double.MAX_VALUE;
  private Double increment = 1d;
  /** Keeps a history of all the data values created as input from this value range. */
  protected List<Number> history = new ArrayList<Number>();
  protected List<Number> optimizerHistory = new ArrayList<Number>();
  /** The strategy for data generation. */
  private DataGenerationAlgorithm algorithm = DataGenerationAlgorithm.RANDOM;

  public ValueRange(Number min, Number max) {
    this.min = min.doubleValue();
    this.max = max.doubleValue();
  }

  public void setIncrement(Number increment) {
    this.increment = increment.doubleValue();
  }

  public Number min() {
    return min;
  }

  public void setMin(Number min) {
    this.min = min.doubleValue();
  }

  public Number max() {
    return max;
  }

  public void setMax(Number max) {
    this.max = max.doubleValue();
  }

  public Collection<Number> getHistory() {
    return history;
  }

  public void setAlgorithm(DataGenerationAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  private Number next(DataType type) {
    double min = min().doubleValue();
    double max = max().doubleValue();
    Number value = null;
    if (algorithm == DataGenerationAlgorithm.ORDERED_LOOP) {
      double last = min;
      if (history.size() > 0) {
        last = history.get(history.size()-1).doubleValue();
      }
      value = last+increment;
    } else if (algorithm == DataGenerationAlgorithm.OPTIMIZED_RANDOM) {
      do {
        value = cDouble(min, max);
        //conver to int or long as needed, double is already in correct format
        if (type == DataType.INT) {
          value = value.intValue();
        } else if (type == DataType.LONG) {
          value = value.longValue();
        }
      } while (optimizerHistory.contains(value));
      optimizerHistory.add(value);
      if (optimizerHistory.size() == (max-min)+1) {
        optimizerHistory.clear();
      }
    } else {
      //default to random
      value = cDouble(min, max);
    }
    history.add(value);
    return value;
  }

  /**
   * Generates a new double precision value in this value range.
   *
   * @return Generated input value.
   */
  public double nextDouble() {
    double min = min().doubleValue();
    double max = max().doubleValue();
    double value = 0;
    if (algorithm == DataGenerationAlgorithm.ORDERED_LOOP) {
      double last = min;
      if (history.size() > 0) {
        last = history.get(history.size()-1).doubleValue();
      }
      value = last+increment;
    } else if (algorithm == DataGenerationAlgorithm.OPTIMIZED_RANDOM) {
      do {
        value = cDouble(min, max);
      } while (optimizerHistory.contains(value));
      optimizerHistory.add(value);
      if (optimizerHistory.size() == (max-min)+1) {
        optimizerHistory.clear();
      }
    } else {
      //default to random
      value = cDouble(min, max);
    }
    history.add(value);
    return value;
  }

  /**
   * Generates a new integer value in this value range.
   *
   * @return Generated input value.
   */
  public int nextInt() {
    int min = min().intValue();
    int max = max().intValue();
    int value = 0;

    if (algorithm == DataGenerationAlgorithm.ORDERED_LOOP) {
      int last = min;
      if (history.size() > 0) {
        last = history.get(history.size()-1).intValue();
      }
      value = last+increment.intValue();
    } else if (algorithm == DataGenerationAlgorithm.OPTIMIZED_RANDOM) {
      do {
        value = cInt(min, max);
      } while (optimizerHistory.contains(value));
      optimizerHistory.add(value);
      if (optimizerHistory.size() == (max-min)+1) {
        optimizerHistory.clear();
      }
    } else {
      //default to random
      value = cInt(min, max);
    }
    history.add(value);
    return value;
  }

  /**
   * Generates a new long value in this value range.
   *
   * @return Generated input value.
   */
  public long nextLong() {
    long min = min().longValue();
    long max = max().longValue();
    long value = 0;

    if (algorithm == DataGenerationAlgorithm.ORDERED_LOOP) {
      long last = min;
      if (history.size() > 0) {
        last = history.get(history.size()-1).intValue();
      }
      value = last+increment.longValue();
    } else if (algorithm == DataGenerationAlgorithm.OPTIMIZED_RANDOM) {
      do {
        value = cLong(min, max);
      } while (optimizerHistory.contains(value));
      optimizerHistory.add(value);
      if (optimizerHistory.size() == (max-min)+1) {
        optimizerHistory.clear();
      }
    } else {
      //default to random
      value = cLong(min, max);
    }
    history.add(value);
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ValueRange interval = (ValueRange) o;

    if (Double.compare(interval.max, max) != 0) return false;
    if (Double.compare(interval.min, min) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = min != +0.0d ? Double.doubleToLongBits(min) : 0L;
    result = (int) (temp ^ (temp >>> 32));
    temp = max != +0.0d ? Double.doubleToLongBits(max) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public boolean contains(Number value) {
    double d = value.doubleValue();
    return d <= max && d >= min;
  }

  @Override
  public String toString() {
    return "min (" + min + "), max (" + max + ')';
  }
}
