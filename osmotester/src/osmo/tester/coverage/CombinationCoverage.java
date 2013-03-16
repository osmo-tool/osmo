package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Provides a variable value that represents a combination of the given set of other variables.
 * Typical use is to create a {@link ScoreConfiguration} and use the methods provided there to directly
 * add the combinations of interest.
 * Thus, the user typically does not use this class directly, although it is possible if desired.
 *
 * @author Teemu Kanstren
 */
public class CombinationCoverage implements CoverageCalculator {
  private static Logger log = new Logger(CombinationCoverage.class);
  /** The names of the variables this is calculating for. */
  private List<String> inputs = new ArrayList<>();
  /** The name of the variable being produced. */
  protected final String outputName;
  /** The set of input variables. Key = variable name, value = the list of values for the variable, in order. */
  private Map<String, Collection<String>> variables = new HashMap<>();

  /**
   * This clones the given parent. Required for concurrent processing as otherwise the definitions are shared
   * through the common {@link ScoreConfiguration} object and everything breaks..
   *
   * @param parent The one to clone from.
   */
  public CombinationCoverage(CombinationCoverage parent) {
    inputs.addAll(parent.inputs);
    outputName = parent.outputName;
  }

  /** @param inputs The set of input variable names. */
  public CombinationCoverage(String... inputs) {
    if (inputs.length == 0) {
      throw new IllegalArgumentException("You must specify some input variables to combine.");
    }
    for (String input : inputs) {
      if (input == null) {
        throw new NullPointerException("Input name cannot be null.");
      }
      if (this.inputs.contains(input)) {
        throw new IllegalArgumentException("Cannot create a combination of a variable with itself:" + input);
      }
      this.inputs.add(input);
    }
    //we sort everything to get a deterministic order in defining and calculating the values
    Collections.sort(this.inputs);
    this.outputName = ScoreConfiguration.combine(this.inputs);
  }

  @Override
  public List<String> getInputNames() {
    return inputs;
  }

  @Override
  public ModelVariable process(TestStep step) {
    log.debug("Processing a new test step.");
    //have to clone for all the sharing of the configuration or we mess with concurrency
    CombinationCoverage clone = new CombinationCoverage(this);
    clone.initVariables();

    log.debug("capturing all input values for the step");
    clone.captureInputValues(step);

    clone.fillMissingVariables();

    //create a new modelvariable to represent the created coverage information (the new combination variable)
    ModelVariable result = new ModelVariable(outputName);

    clone.addCombinations(result, inputs, new ArrayList<>());
    return result;
  }

  /**
   * Go through the given test step and collect the values for all the variables defined for this coverage criteria
   * to form a new value.
   *
   * @param step The step with values to process.
   */
  private void captureInputValues(TestStep step) {
    Collection<ModelVariable> stepValues = step.getValues();
    for (ModelVariable mv : stepValues) {
      String name = mv.getName();
      if (!inputs.contains(name)) {
        continue;
      }
      Collection<Object> values = mv.getValues();
      for (Object value : values) {
        variables.get(name).add("" + value);
      }
    }
  }

  /** If the step has no value for some variable we need, put a null value for it. */
  private void fillMissingVariables() {
    for (String input : inputs) {
      Collection<String> values = this.variables.get(input);
      if (values.isEmpty()) {
        log.debug("no value for:" + input);
        values.add("null");
      }
    }
  }

  /** Empties the set of values for all variables. */
  private void initVariables() {
    variables.clear();
    for (String input : inputs) {
      Collection<String> values = new HashSet<>();
      variables.put(input, values);
    }
  }

  /**
   * Recursively goes through the list of input variables and creates the combination variable values.
   * Starts with the first variable in the (sorted) set of input variables to combine.
   * Takes the values of that variable. For each one, combines it with all combinations of other variables.
   * Does this until all combinations are done.
   * Note that a combination is considered the same regardless of the order of the variables, thus, for example,
   * "bob"+"john" is the same as "john"+"bob" and only the "bob"+"john" combination is created and stored.
   *
   * @param result   The coverage variable where the values are stored.
   * @param myInputs Sorted set of input variable names. Reduces on every recursive iteration.
   * @param myValues This is where the different values for the different variables to combine are stored.
   */
  private void addCombinations(ModelVariable result, List<String> myInputs, List<Object> myValues) {
    List<String> inputClone = new ArrayList<>();
    inputClone.addAll(myInputs);
    //go through the variables in order, processing each one once for each combination
    //so if we have an input set of "bob", "john", "teemu", we start with "bob", combine all values of "bob"
    //with all values of "john" and all these combinations with all values of "teemu".
    String input = inputClone.remove(0);
    Collection<String> values = variables.get(input);
    for (Object value : values) {
      List<Object> valueClone = new ArrayList<>();
      valueClone.addAll(myValues);
      valueClone.add(value);
      if (inputClone.isEmpty()) {
        //we come here when we have reach the end of one iteration of recursion.
        //the number of times we come here is a multiplication of the number of values of all input variables
        String combination = ScoreConfiguration.combine(valueClone);
        log.debug("new combination created:" + combination);
        result.addValue(combination, true);
      } else {
        //not a final iteration of all input variables, so continue to build the set
        addCombinations(result, inputClone, valueClone);
      }
    }
  }

  @Override
  public String toString() {
    return outputName + " = " + inputs;
  }

  public String getOutputName() {
    return outputName;
  }
}


