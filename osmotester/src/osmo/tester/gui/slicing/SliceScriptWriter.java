package osmo.tester.gui.slicing;

import osmo.common.log.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Used for writing the DSM script from the DSM GUI to a file.
 *
 * @author Teemu Kanstren
 */
public class SliceScriptWriter {
  private static final Logger log = new Logger(SliceScriptWriter.class);

  /**
   * @param algorithm        The test generation algorithm to use in processing the DSM.
   * @param modelFactory     The (fully qualified) name of the class to use for creating the model objects.
   * @param transitionRules  Defined rules considering transitions.
   * @param variableCoverage Defined rules for variable coverage.
   * @param variableValues   Defined options for variable values.
   */
  public void write(String algorithm, String modelFactory, List<String> transitionRules, List<String> variableCoverage, List<String> variableValues) {
    BufferedWriter fw = null;
    try {
      fw = new BufferedWriter(new FileWriter("osmo-dsl.txt"));
      fw.write("setting, value");
      fw.newLine();
      fw.write("algorithm, ");
      fw.write(algorithm);
      fw.newLine();
      fw.write("model factory, ");
      fw.write(modelFactory);
      fw.newLine();
      fw.newLine();
      fw.write("step, times");
      fw.newLine();
      for (String line : transitionRules) {
        fw.write(line);
        fw.newLine();
      }
      fw.newLine();
      fw.write("variable, coverage");
      fw.newLine();
      for (String line : variableCoverage) {
        fw.write(line);
        fw.newLine();
      }
      fw.newLine();
      fw.write("variable, values");
      fw.newLine();
      for (String line : variableValues) {
        fw.write(line);
        fw.newLine();
      }
      fw.newLine();
    } catch (IOException e) {
      log.error("Error writing script to file", e);
      throw new RuntimeException("Error writing script to file", e);
    } finally {
      if (fw != null) {
        try {
          fw.close();
        } catch (IOException e) {
          //blaablaa lets just ignore it since errors should be known already and other exceptions thrown..
        }
      }
    }
  }
}
