package osmo.tester.coverage;

import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestStep;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Categorizes a set of values for an integer variable into a smaller subset of coverage values.
 * For example, all values betweel 2-N can be replaced with a value of "many".
 * Thus only one value in this range needs to be covered to cover the whole set.
 * In academic terms this is called "equivalence partitioning" or "category partitioning".
 * The ranges/categories can be overlapping in which case many values may be generated for a single value.
 * For example, 1-1="one", 1-5="some", observing 1 will produce "one" and "some" coverage values.
 * Notice that if an input value is observed that is not part of any defined category, the value is simply
 * discarded.
 *
 * @author Teemu Kanstren
 */
public class RangeCategory implements CoverageCalculator {
  /** The names of the input variable this range is for. */
  protected final String inputName;
  /** The name of the variable being produced. */
  protected final String outputName;
  /** The set of ranges or categories for this coverage variable. */
  private Collection<IntegerRange> ranges = new ArrayList<>();

  /** @param inputName The input variable name. */
  public RangeCategory(String inputName, String outputName) {
    if (inputName == null) {
      throw new NullPointerException("Range name cannot be null.");
    }
    this.inputName = inputName;
    this.outputName = outputName;
  }

  /**
   * Adds a new range category with the given specs.
   *
   * @param min  Range minimum.
   * @param max  Range maximum.
   * @param name Range name, used to replace values in the range.
   */
  public IntegerRange addCategory(int min, int max, String name) {
    IntegerRange range = new IntegerRange(min, max, name);
    ranges.add(range);
    return range;
  }

  public Collection<String> getInputNames() {
    Collection<String> names = new ArrayList<>();
    names.add(inputName);
    return names;
  }

  public String getInputName() {
    return inputName;
  }

  @Override
  public ModelVariable process(TestStep step) {
    ModelVariable result = new ModelVariable(outputName);
    Collection<ModelVariable> variables = step.getValues();
    for (ModelVariable mv : variables) {
      String name = mv.getName();
      if (!name.equals(inputName)) {
        continue;
      }
      Collection<Object> values = mv.getValues();
      for (Object value : values) {
        process(result, value);
      }
    }
    return result;
  }

  /**
   * Process the values, replacing numbers with defined ranges where available.
   *
   * @param result This is where the output values are stored.
   * @param o      The input value to process. Should be integer or will fail..
   */
  protected void process(ModelVariable result, Object o) {
    Integer value;
    try {
      value = (Integer) o;
    } catch (ClassCastException e) {
      String type = null;
      if (o != null) {
        type = o.getClass().toString();
      }
      throw new RuntimeException("Wrong type of value for RangeCategory expected Integer got: " + type, e);
    }
    for (IntegerRange range : ranges) {
      if (value >= range.min && value <= range.max) {
        result.addValue(range.name, true);
      }
    }
  }

  /**
   * Access to the defined range categories.
   *
   * @return The defined range categories.
   */
  public Collection<IntegerRange> getRanges() {
    return ranges;
  }

  public Collection<String> getOptions() {
    Collection<String> options = new ArrayList<>();
    for (IntegerRange range : ranges) {
      options.add(range.getName());
    }
    return options;
  }

  public String getOutputName() {
    return outputName;
  }

  @Override
  public String toString() {
    return "RangeCategory{" +
            "inputName='" + inputName + '\'' +
            ", outputName='" + outputName + '\'' +
            ", ranges=" + ranges +
            '}';
  }
}
