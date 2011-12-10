package osmo.tester.gui.dsm;

import osmo.common.log.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/** @author Teemu Kanstren */
public class DSMScriptWriter {
  private static final Logger log = new Logger(DSMScriptWriter.class);

  public void write(String algorithm, String modelFactory, List<String> transitionRules, List<String> variableRules) {
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
      for (String line : variableRules) {
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
