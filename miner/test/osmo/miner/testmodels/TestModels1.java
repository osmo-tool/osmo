package osmo.miner.testmodels;

import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;
import osmo.miner.model.program.Suite;

/**
 * @author Teemu Kanstren
 */
public class TestModels1 {
  public static Suite model1() {
    Suite model = new Suite();

    Program program1 = new Program("TestProgram1");
    program1.addVariable("v1", "hello");
    Step step1_1 = program1.createStep("step1");
    step1_1.addVariable("s1v1", "1");
    step1_1.addVariable("s1v2", "2");
    Step step1_2 = program1.createStep("step2");
    step1_2.addVariable("s2v1", "4");
    step1_2.addVariable("s2v2", "2");
    Step step1_3 = program1.createStep("step3");
    step1_3.addVariable("s3v1", "3");
    step1_3.addVariable("s3v2", "4");
    Step step1_4 = program1.createStep("step4");
    step1_4.addVariable("s4v1", "1");
    step1_4.addVariable("s4v2", "2");

    Program program2 = new Program("TestProgram2");
    Step step2_1 = program2.createStep("step1");
    Step step2_2 = program2.createStep("step2");
    step2_2.addVariable("s2v1", "7");
    Step step2_3 = program2.createStep("step3");
    step2_3.addVariable("s3v1", "5");
    Step step2_4 = program2.createStep("step4");
    model.add(program1);
    model.add(program2);
    return model;
  }

  public static Suite modelWithOverlapError() {
    Suite model = new Suite();

    Program program1 = new Program("TestProgram1");
    program1.addVariable("v1", "hello");
    Step step1_1 = program1.createStep("step1");
    step1_1.addVariable("s1v1", "1");
    step1_1.addVariable("s1v1", "2");
    Step step1_2 = program1.createStep("step2");
    step1_2.addVariable("s2v1", "4");
    step1_2.addVariable("s2v1", "2");
    Step step1_3 = program1.createStep("step3");
    step1_3.addVariable("s3v1", "3");
    step1_3.addVariable("s3v1", "4");
    Step step1_4 = program1.createStep("step4");
    step1_4.addVariable("s4v1", "1");
    step1_4.addVariable("s4v1", "2");

    Program program2 = new Program("TestProgram2");
    Step step2_1 = program2.createStep("step1");
    Step step2_2 = program2.createStep("step2");
    step2_2.addVariable("s2v1", "7");
    Step step2_3 = program2.createStep("step3");
    step2_3.addVariable("s3v1", "5");
    Step step2_4 = program2.createStep("step4");
    model.add(program1);
    model.add(program2);
    return model;
  }
}
