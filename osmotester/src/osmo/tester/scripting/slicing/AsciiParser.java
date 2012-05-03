package osmo.tester.scripting.slicing;

import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.scripting.AbstractAsciiParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses model slicing definitions in ASCII format. The expected format is the following:
 * <p/>
 * ---begin example---
 * <p/>
 * setting, value
 * model factory, osmo.tester.slicing.TestModelFactory
 * algorithm, random
 * <p/>
 * step, times
 * add event, >=1
 * <p/>
 * variable, values
 * start time, 5
 * event count, any
 * <p/>
 * --- end example ---
 * <p/>
 * There are three different tables in this format. It is required to provide the settings table and
 * one or both of the steps and variables tables.
 * <p/>
 * The settings table has the following elements:
 * model factory = The fully qualified name of a class implementing {@link osmo.tester.scripting.OSMOConfigurationFactory}.
 * algorithm = Fully qualified name of used test generation algorithm, or "random"/"weighted random"/"balancing"
 * seed = Random seed to be used by OSMOTester
 * <p/>
 * The steps table defines which steps should be covered by all generated tests, the form is:
 * "step name", "number of times to cover". Step name must match a name of a transition in a model object.
 * <p/>
 * The variables table defines which variable values should be covered by all generated tests. The form is:
 * "variable name", "required value". The value comparison is based on String.equals() method. To provide several
 * values for a single variable, repeat several lines for that variable with different values.  Some of the built-in data
 * objects support "all", meaning all values in ValueRange or ValueSet should be covered.
 * <p/>
 * Steps practically equal the transition names and can be defined to have to be present the number of times specified
 * exactly (add event, == 1), minimum of that (add event, >= 1) or maximum of that (add event, <= 1)
 *
 * @author Teemu Kanstren
 */
public class AsciiParser extends AbstractAsciiParser {
  private static Logger log = new Logger(AsciiParser.class);
  /** Configuration for test generation, as parsed from the ASCII input. */
  private SlicingConfiguration config = new SlicingConfiguration();
  
  public SlicingConfiguration loadAndParse(String fileName) throws IOException {
    FileInputStream in = new FileInputStream(fileName);
    String script = TestUtils.getResource(in);
    return parse(script);
  }

  private static enum Relation {
    MIN, MAX, EXACT
  }

  public AsciiParser() {
    super(log);
  }

  /**
   * Parses the given input to provide a test generation configuration.
   *
   * @param input The input to be parsed.
   * @return The configuration for test generation as defined by the given input.
   */
  public SlicingConfiguration parse(String input) {
    String[] lines = parseLines(input);
    parseSettingsTable(lines);
    parseStepTable(lines);
    parseVariableValueTable(lines);
    parseVariableCoverageTable(lines);
    config.validate();
    log.debug("Parsing done");
    return config;
  }

  /**
   * Parse the test generation settings.
   *
   * @param lines The table text lines.
   */
  private void parseSettingsTable(String[] lines) {
    String[] settings = parseTable(lines, "setting", "value");
    String algorithm = null;
    String factory = null;
    for (int i = 0; i < settings.length; i += 2) {
      String name = settings[i];
      String value = settings[i + 1];
      log.debug("Setting found:" + name + " = " + value);
      if (name.equalsIgnoreCase("algorithm")) {
        if (algorithm != null) {
          throw new IllegalArgumentException("Only one algorithm allowed.");
        }
        algorithm = value;
        config.setAlgorithm(algorithm);
      }
      if (name.equalsIgnoreCase("model factory") || name.equalsIgnoreCase("modelfactory") || name.equalsIgnoreCase("model-factory")) {
        if (factory != null) {
          throw new IllegalArgumentException("Only one model factory allowed.");
        }
        factory = value;
        config.setModelFactory(value);
      }
      if (name.equalsIgnoreCase("seed")) {
        try {
          long seed = Long.parseLong(value);
          config.setSeed(seed);
        } catch (NumberFormatException e) {
          throw new NumberFormatException("Seed value should be parseable to long integer, was:" + value + ".");
        }
      }
    }
  }

  /**
   * Parses the test step coverage requirements.
   *
   * @param lines Text contents for the table.
   */
  private void parseStepTable(String[] lines) {
    log.debug("parsing steps");
    String[] steps = parseTable(lines, "step", "times");
    log.debug("steps:" + steps.length);
    for (int i = 0; i < steps.length; i += 2) {
      String name = steps[i];
      String times = steps[i + 1];
      if (times.length() < 3) {
        throw new IllegalArgumentException("Step count must begin with <=(max), >=(min) or ==(exact).");
      }
      Relation r = parseMin(times);
      times = times.substring(2);
      times = times.trim();
      int count = parseTimes(name, times, r);
      switch (r) {
        case MIN:
          config.addStepMin(name, count);
          break;
        case MAX:
          config.addStepMax(name, count);
          break;
        case EXACT:
          config.addStepMin(name, count);
          config.addStepMax(name, count);
          break;
        default:
          throw new IllegalStateException("Unknown relation for step size in DSM definition.");
      }
      log.debug("Step requirement found:" + name + " " + r.toString() + ":" + count + " times " + times);
    }
  }

  private Relation parseMin(String times) {
    char ch1 = times.charAt(0);
    char ch2 = times.charAt(1);
    if (ch2 != '=') {
      throw new IllegalArgumentException("Step count must begin with <=(max) or >=(min).");
    }
    if (ch1 == '<') {
      return Relation.MAX;
    }
    if (ch1 == '>') {
      return Relation.MIN;
    }
    if (ch1 == '=') {
      return Relation.EXACT;
    }
    throw new IllegalArgumentException("Step count must begin with <=(max) or >=(min).");
  }

  /**
   * Parse a number for how many times a step should be covered.
   *
   * @param name The name of the step. Used for error reporting.
   * @param from The text from which the number should be parsed.
   * @param r    The relation, as exact is allowed value 0 but others not.
   * @return The number of times to cover the step.
   */
  private int parseTimes(String name, String from, Relation r) {
    try {
      int times = Integer.parseInt(from);
      if (times < 0 || (times == 0 && r != Relation.EXACT)) {
        throw new IllegalArgumentException("Number of times the steps is required needs to be > 0. Was " + from + ".");
      }
      return times;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Number of times should be integer, was [" + from + "] for step [" + name + "].", e);
    }
  }

  /**
   * Parses the variable coverage requirements.
   *
   * @param lines Text lines for the variable table.
   */
  private void parseVariableCoverageTable(String[] lines) {
    log.debug("parsing variable coverage");
    String[] variables = parseTable(lines, "variable", "coverage");
    Map<String, DataCoverageRequirement> map = new HashMap<>();
    for (int i = 0; i < variables.length; i += 2) {
      String name = variables[i].trim();
      String value = variables[i + 1].trim();
      DataCoverageRequirement req = map.get(name);
      if (req == null) {
        req = new DataCoverageRequirement(name);
        map.put(name, req);
        config.add(req);
      }
      req.addRequirement(value);
      log.debug("Variable requirement found:" + req);
    }
  }

  /**
   * Parses the variable values for test generation.
   *
   * @param lines Text lines for the variable table.
   */
  private void parseVariableValueTable(String[] lines) {
    log.debug("parsing variable scripting");
    String[] variables = parseTable(lines, "variable", "values");
    if (variables.length == 0) {
      return;
    }
    ScriptedValueProvider scripter = new ScriptedValueProvider();
    config.setScripter(scripter);

    for (int i = 0; i < variables.length; i += 2) {
      String name = variables[i].trim();
      String value = variables[i + 1].trim();
      scripter.addValue(name, value);
      log.debug("Variable value found:" + name + " = " + value);
    }
  }
}
