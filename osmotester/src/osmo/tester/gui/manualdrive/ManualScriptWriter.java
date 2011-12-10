package osmo.tester.gui.manualdrive;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class ManualScriptWriter {
  private static final Logger log = new Logger(ManualScriptWriter.class);

  public void write(TestSuite suite) {
    BufferedWriter fw = null;
    try {
      fw = new BufferedWriter(new FileWriter("osmo-tests.txt"));
      fw.write("action, name, value");
      fw.newLine();
      List<TestCase> tests = suite.getAllTestCases();
      for (TestCase test : tests) {
        fw.write("new test,,\n");
        List<TestStep> steps = test.getSteps();
        for (TestStep step : steps) {
          fw.write("step,");
          fw.write(step.getTransition().getName());
          fw.write(",");
          fw.newLine();
          List<ModelVariable> variables = step.getVariableValues();
          for (ModelVariable variable : variables) {
            fw.write("variable,");
            fw.write(variable.getName());
            fw.write(",");
            fw.write(variable.getValues().iterator().next().toString());
            fw.newLine();
          }
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
