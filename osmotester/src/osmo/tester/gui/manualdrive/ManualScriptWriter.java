package osmo.tester.gui.manualdrive;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Writes the manually defined script to a file.
 *
 * @author Teemu Kanstren
 */
public class ManualScriptWriter {
  private static final Logger log = new Logger(ManualScriptWriter.class);
  public static String FILENAME = "osmo-tests.txt";

  //TODO: fix this
  public void write(TestSuite suite) {
    BufferedWriter fw = null;
    try {
      fw = new BufferedWriter(new FileWriter(FILENAME));
      fw.write("action, name, value");
      fw.newLine();
      List<TestCase> tests = suite.getAllTestCases();
      for (TestCase test : tests) {
        fw.write("new test,,");
        fw.newLine();
        List<TestCaseStep> steps = test.getSteps();
        for (TestCaseStep step : steps) {
          fw.write("step,");
          fw.write(step.getName());
          fw.write(",");
          fw.newLine();
//          Collection<ModelVariable> variables = step.getValues();
//          for (ModelVariable variable : variables) {
//            fw.write("variable,");
//            fw.write(variable.getName());
//            fw.write(",");
//            fw.write(variable.getValues().iterator().next().toString());
//            fw.newLine();
//          }
        }
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
