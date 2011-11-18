package osmo.tester.dsm;

import osmo.common.log.Logger;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses test definitions in ASCII format. The expected format is the following:
 *
 * ---begin example---
 *
 * setting, value
 * model factory, osmo.tester.dsm.TestModelFactory
 * algorithm, random
 *
 * step, times
 * add event, 1
 *
 * variable, values
 * start time, 5
 * event count, any
 *
 * --- end example ---
 *
 * There are three different tables in this format. It is required to provide the settings table and
 * one or both of the steps and variables tables.
 *
 * The settings table has the following elements:
 * model factory = The fully qualified name of a class implementing {@link osmo.tester.dsm.ModelObjectFactory}.
 * algorithm = Fully qualified name of used test generation algorithm, or "random"/"weighted random"/"optimized random"
 * seed = Random seed to be used by OSMOTester
 *
 * The steps table defines which steps should be covered by all generated tests, the form is:
 * "step name", "number of times to cover". Step name must match a name of a transition in a model object.
 *
 * The variables table defines which variable values should be covered by all generated tests. The form is:
 * "variable name", "required value". The value comparison is based on String.equals() method. To provide several
 * values for a single variable, repeat several lines for that variable with different values. "any" is a special
 * value meaning that the variable should be covered but the value does not matter. Some of the built-in data
 * objects support "all", meaning all values in ValueRange or ValueSet should be covered.
 *
 * @author Teemu Kanstren
 */
public class AsciiParser {
  private static Logger log = new Logger(AsciiParser.class);
  /** Configuration for test generation, as parsed from the ASCII input. */
  private DSMConfiguration config = new DSMConfiguration();

  /**
   * Parses the given input to provide a test generation configuration.
   *
   * @param input The input to be parsed.
   * @return The configuration for test generation as defined by the given input.
   */
  public DSMConfiguration parse(String input) {
    String[] lines = parseLines(input);
    parseSettingsTable(lines);
    parseStepTable(lines);
    parseVariableTable(lines);
    validateConfiguration();
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
    for (int i = 0 ; i < settings.length ; i += 2) {
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
          throw new NumberFormatException("Seed value should be parseable to long integer, was:"+value);
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
    for (int i = 0 ; i < steps.length ; i += 2) {
      String name = steps[i];
      int times = parseTimes(name, steps[i + 1]);
      for (int t = 0 ; t < times ; t++) {
        config.addStep(name);
      }
      log.debug("Step requirement found:" + name + " times " + times);
    }
  }

  /**
   * Parse a number for how many times a step should be covered.
   *
   * @param name The name of the step. Used for error reporting.
   * @param from The text from which the number should be parsed.
   * @return The number of times to cover the step.
   */
  private int parseTimes(String name, String from) {
    try {
      int times = Integer.parseInt(from);
      if (times <= 0) {
        throw new IllegalArgumentException("Number of times the steps is required needs to be > 0. Was " + from);
      }
      return times;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Number of times should be integer, was [" + from + "] for step [" + name + "]", e);
    }
  }

  /**
   * Parses the variable coverage requirements.
   *
   * @param lines Text lines for the variable table.
   */
  private void parseVariableTable(String[] lines) {
    log.debug("parsing variables");
    String[] variables = parseTable(lines, "variable", "values");
    Map<String, DataCoverageRequirement> map = new HashMap<String, DataCoverageRequirement>();
    for (int i = 0 ; i < variables.length ; i += 2) {
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
   * Generic parse for all the tables. Supports only 2 column tables, where each column must have content.
   *
   * @param lines The lines (rows) of the table.
   * @param h1 Header of first column.
   * @param h2 Header of the second column.
   * @return The cells of the table, first row as cells[0], cells[1], second row as cells[2], cells[3], etc.
   */
  public String[] parseTable(String[] lines, String h1, String h2) {
    List<String> temp = new ArrayList<String>();
    //this "i" holds the index of overall parsing
    int i = 0;
    boolean found = false;
    //first we proceed until we find the header
    for ( ; i < lines.length ; i++) {
      String line = lines[i];
      log.debug("parsing line:" + line);
      String[] cells = line.split(",");
      if (cells.length < 2) {
        continue;
      }
      cells[0] = cells[0].trim();
      cells[1] = cells[1].trim();
      if (cells[0].equalsIgnoreCase(h1) && cells[1].equalsIgnoreCase(h2)) {
        log.debug("found table header");
        found = true;
        break;
      }
    }
    //if we find no header, we return an empty set of cells
    if (!found) {
      return new String[0];
    }
    //table name here is for error reporting
    String tableName = "\"" + h1 + ", " + h2 + "\"";
    //now we parse all cells
    for (i += 1; i < lines.length ; i++) {
      //the table cells must be separated with a comma
      String[] cells = lines[i].split(",");
      String error = "Table rows must have 2 cells. " + tableName + " had a row with " + cells.length + " cell(s).";
      //the table must have exactly 2 cells on each row
      if (cells.length > 2) {
        throw new IllegalArgumentException(error);
      }
      if (cells.length == 1) {
        //empty line ends the table
        if (cells[0].length() == 0) {
          //table ends
          break;
        }
        //the table must have exactly 2 cells on each row
        throw new IllegalArgumentException(error);
      }
      //trim any excess whitespace from the cell data
      cells[0] = cells[0].trim();
      cells[1] = cells[1].trim();
      temp.add(cells[0]);
      temp.add(cells[1]);
    }
    if (temp.size() == 0) {
      //empty tables are not supported
      throw new IllegalArgumentException("Table " + tableName + " has no content.");
    }
    String[] result = new String[temp.size()];
    return temp.toArray(result);
  }

  /**
   * Get a list of rows in given input, as separated by the different potential line separators (unix/windows/mac).
   *
   * @param input The text to be parsed.
   * @return The list of rows separated by linefeeds.
   */
  private String[] parseLines(String input) {
    String[] split = input.split("\n|\r\n|\r");
    for (int i = 0 ; i < split.length ; i++) {
      String s = split[i];
      split[i] = s.trim();
    }
    return split;
  }

  /**
   * Validate the overall parsed configuration.
   */
  private void validateConfiguration() {
    String errors = "";
    if (!config.hasRequiments()) {
      errors += "Input does not define any valid coverage requirements (steps or variables).";
    }
    if (config.getModelFactory() == null) {
      errors += "Input does not define model object factory.";
    }
    if (errors.length() > 0) {
      throw new IllegalArgumentException(errors);
    }
  }
}
