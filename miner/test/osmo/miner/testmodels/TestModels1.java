package osmo.miner.testmodels;

import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;
import osmo.miner.model.program.Suite;
import osmo.miner.model.program.Variable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class TestModels1 {
  public static Suite model1() {
    Suite model = new Suite();
    Program program1 = new Program("TestProgram1");
    createVariable(program1, "v1", "hello", "world");
    Step step1_1 = program1.createStep("step1");
    createVariable(step1_1, "s1v1", "1");
    createVariable(step1_1, "s1v1", "2");
    Step step1_2 = program1.createStep("step2");
    createVariable(step1_2, "s2v1", "4");
    createVariable(step1_2, "s2v1", "2");
    Step step1_3 = program1.createStep("step3");
    createVariable(step1_3, "s3v1", "3");
    createVariable(step1_3, "s3v1", "4");
    Step step1_4 = program1.createStep("step4");
    createVariable(step1_4, "s4v1", "1");
    createVariable(step1_4, "s4v1", "2");
    Program program2 = new Program("TestProgram2");
    Step step2_1 = program2.createStep("step1");
    Step step2_2 = program2.createStep("step2");
    createVariable(step2_2, "s2v1", "7");
    Step step2_3 = program2.createStep("step3");
    createVariable(step2_3, "s3v1", "5");
    Step step2_4 = program2.createStep("step4");
    model.add(program1);
    model.add(program2);
    return model;
  }

  private static void createVariable(Program p, String name, String... values) {
    Variable var = p.createVariable(name);
    for (String value : values) {
      var.addValue(value);
    }
  }

  private static void createVariable(Step s, String name, String... values) {
    Variable var = s.createVariable(name);
    for (String value : values) {
      var.addValue(value);
    }
  }
}
