package osmo.tester.dsm;

import osmo.common.log.Logger;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Teemu Kanstren */
public class AsciiParser {
  private static Logger log = new Logger(AsciiParser.class);
  private DSMConfiguration reqs = new DSMConfiguration();

  public DSMConfiguration parse(String input) {
    String[] lines = parseLines(input);
    parseSettingsTable(lines);
    parseStepTable(lines);
    parseVariableTable(lines);
    validateConfiguration();
    return reqs;
  }

  private void parseSettingsTable(String[] lines) {
    String[] settings = parseTable(lines, "setting", "value");
    String algorithm = null;
    String factory = null;
    for (int i = 0 ; i < settings.length ; i += 2) {
      String name = settings[i];
      String value = settings[i + 1];
      log.debug("Setting found:" + name + " = " + value);
      if (name.equals("algorithm")) {
        if (algorithm != null) {
          throw new IllegalArgumentException("Only one algorithm allowed.");
        }
        algorithm = value;
        reqs.setAlgorithm(algorithm);
      }
      if (name.equals("model factory")) {
        if (factory != null) {
          throw new IllegalArgumentException("Only one model factory allowed.");
        }
        factory = value;
        reqs.setModelFactory(value);
      }
    }
  }

  private void parseStepTable(String[] lines) {
    log.debug("parsing steps");
    String[] steps = parseTable(lines, "step", "times");
    log.debug("steps:" + steps.length);
    for (int i = 0 ; i < steps.length ; i += 2) {
      String name = steps[i];
      int times = parseTimes(name, steps[i + 1]);
      for (int t = 0 ; t < times ; t++) {
        reqs.addStep(name);
      }
      log.debug("Step requirement found:" + name + " times " + times);
    }
  }

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
        reqs.add(req);
      }
      req.addRequirement(value);
      log.debug("Variable requirement found:" + req);
    }
  }

  public String[] parseTable(String[] lines, String h1, String h2) {
    List<String> temp = new ArrayList<String>();
    int i = 0;
    boolean found = false;
    for ( ; i < lines.length ; i++) {
      String line = lines[i];
      log.debug("parsing line:" + line);
      String[] cells = line.split(",");
      if (cells.length < 2) {
        continue;
      }
      cells[0] = cells[0].trim();
      cells[1] = cells[1].trim();
      if (cells[0].equals(h1) && cells[1].equals(h2)) {
        log.debug("found table header");
        found = true;
        break;
      }
    }
    if (!found) {
      return new String[0];
    }
    String tableName = "\"" + h1 + ", " + h2 + "\"";
    for (i += 1; i < lines.length ; i++) {
      String[] cells = lines[i].split(",");
      String error = "Table rows must have 2 cells. " + tableName + " had a row with " + cells.length + " cell(s).";
      if (cells.length > 2) {
        throw new IllegalArgumentException(error);
      }
      if (cells.length == 1) {
        if (cells[0].length() == 0) {
          //table ends
          break;
        }
        throw new IllegalArgumentException(error);
      }
      cells[0] = cells[0].trim();
      cells[1] = cells[1].trim();
      temp.add(cells[0]);
      temp.add(cells[1]);
    }
    if (temp.size() == 0) {
      throw new IllegalArgumentException("Table " + tableName + " has no content.");
    }
    String[] result = new String[temp.size()];
    return temp.toArray(result);
  }

  private String[] parseLines(String input) {
    String[] split = input.split("\n|\r\n|\r");
    for (int i = 0 ; i < split.length ; i++) {
      String s = split[i];
      split[i] = s.trim();
    }
    return split;
  }

  private void validateConfiguration() {
    String errors = "";
    if (!reqs.hasRequiments()) {
      errors += "Input does not define any valid coverage requirements (steps or variables).";
    }
    if (reqs.getModelFactory() == null) {
      errors += "Input does not define model object factory.";
    }
    if (errors.length() > 0) {
      throw new IllegalArgumentException(errors);
    }
  }
}
