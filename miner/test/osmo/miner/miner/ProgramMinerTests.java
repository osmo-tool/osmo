package osmo.miner.miner;

import org.junit.Before;
import org.junit.Test;
import osmo.miner.Config;
import osmo.miner.log.Logger;
import osmo.miner.miner.program.ProgramModelMiner;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Variable;
import osmo.miner.parser.xml.XmlParser;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class ProgramMinerTests {
  private ProgramModelMiner miner;
  private XmlParser parser;

  @Before
  public void setup() {
//    Logger.debug = true;
    Config.variableId = "variable";
    Config.variableNameId = "name";
    Config.variableValueId = "value";
    Config.stepId = "teststep";
    Config.stepNameId = "name";
    parser = new XmlParser();
    miner = new ProgramModelMiner();
    parser.addMiner(miner);
  }

  @Test
  public void globalVariables() {
    InputStream file1 = getClass().getResourceAsStream("testmodel1.xml");
    parser.parse(file1);
    Program program = miner.getMainProgram();
    Map<String, Variable> variables = program.getGlobalVariables();
    assertEquals("Number of parsed variables", 4, variables.size());
    Variable gv1 = variables.get("gv1");
    Variable gv2 = variables.get("gv2");
    Variable lv1 = variables.get("lv1");
    Variable lv2 = variables.get("lv2");
    assertEquals("Values for gv1", "[gfoo]", gv1.getValues().toString());
    assertEquals("Values for gv2", "[gbar]", gv2.getValues().toString());
    assertEquals("Values for lv1", "[lfoo]", lv1.getValues().toString());
    assertEquals("Values for lv2", "[lbar]", lv2.getValues().toString());
  }

  @Test
  public void singleStepVariables() {
    InputStream file1 = getClass().getResourceAsStream("testmodel1.xml");
    parser.parse(file1);
    Program program = miner.getMainProgram();
    Map<String, Program> steps = program.getSteps();
    Map<String, Variable> variables = program.getVariables();
    assertEquals("Number of parsed variables", 2, variables.size());
    Variable gv1 = variables.get("gv1");
    Variable gv2 = variables.get("gv2");
    Variable lv1 = variables.get("lv1");
    Variable lv2 = variables.get("lv2");
    assertEquals("Values for gv1", "[gfoo]", gv1.getValues().toString());
    assertEquals("Values for gv2", "[gbar]", gv2.getValues().toString());
    assertEquals("Values for lv1", null, lv1);
    assertEquals("Values for lv2", null, lv2);

    Program step1 = steps.get("step1");
    Map<String, Variable> step1Variables = step1.getVariables();
    lv1 = step1Variables.get("lv1");
    lv2 = step1Variables.get("lv2");
    assertEquals("Values for lv1", "[lfoo]", lv1.getValues().toString());
    assertEquals("Values for lv2", "[lbar]", lv2.getValues().toString());
  }

  @Test
  public void twoStepVariables() {
    InputStream file1 = getClass().getResourceAsStream("testmodel2.xml");
    parser.parse(file1);
    Program program = miner.getMainProgram();
    Map<String, Program> steps = program.getSteps();
    Map<String, Variable> variables = program.getVariables();
    assertEquals("Number of parsed variables", 2, variables.size());
    Variable gv1 = variables.get("gv1");
    Variable gv2 = variables.get("gv2");
    Variable lv1 = variables.get("lv1");
    Variable lv2 = variables.get("lv2");
    assertEquals("Values for gv1", "[gfoo]", gv1.getValues().toString());
    assertEquals("Values for gv2", "[gbar]", gv2.getValues().toString());
    assertEquals("Values for lv1", null, lv1);
    assertEquals("Values for lv2", null, lv2);

    Program step1 = steps.get("step1");
    Map<String, Variable> step1Variables = step1.getVariables();
    lv1 = step1Variables.get("lv1");
    lv2 = step1Variables.get("lv2");
    assertEquals("Values for lv1", "[lfoo1]", lv1.getValues().toString());
    assertEquals("Values for lv2", "[lbar1]", lv2.getValues().toString());

    Program step2 = steps.get("step2");
    Map<String, Variable> step2Variables = step2.getVariables();
    lv1 = step2Variables.get("lv1");
    lv2 = step2Variables.get("lv2");
    Variable lv3 = step2Variables.get("lv3");
    assertEquals("Values for lv1", "[lfoo2]", lv1.getValues().toString());
    assertEquals("Values for lv2", "[lbar2]", lv2.getValues().toString());
    assertEquals("Values for lv3", "[llol2]", lv3.getValues().toString());
  }

  @Test
  public void twoModelsWithOverlap() {
    InputStream file1 = getClass().getResourceAsStream("testmodel1.xml");
    InputStream file2 = getClass().getResourceAsStream("testmodel2.xml");
    parser.parse(file1);
    parser.parse(file2);

    Program program = miner.getMainProgram();
    Map<String, Program> steps = program.getSteps();
    Map<String, Variable> variables = program.getVariables();
    assertEquals("Number of parsed variables", 2, variables.size());
    Variable gv1 = variables.get("gv1");
    Variable gv2 = variables.get("gv2");
    Variable lv1 = variables.get("lv1");
    Variable lv2 = variables.get("lv2");
    assertEquals("Values for gv1", "[gfoo, gfoo]", gv1.getValues().toString());
    assertEquals("Values for gv2", "[gbar, gbar]", gv2.getValues().toString());
    assertEquals("Values for lv1", null, lv1);
    assertEquals("Values for lv2", null, lv2);

    Program step1 = steps.get("step1");
    Map<String, Variable> step1Variables = step1.getVariables();
    lv1 = step1Variables.get("lv1");
    lv2 = step1Variables.get("lv2");
    assertEquals("Values for lv1", "[lfoo, lfoo1]", lv1.getValues().toString());
    assertEquals("Values for lv2", "[lbar, lbar1]", lv2.getValues().toString());

    Program step2 = steps.get("step2");
    Map<String, Variable> step2Variables = step2.getVariables();
    lv1 = step2Variables.get("lv1");
    lv2 = step2Variables.get("lv2");
    Variable lv3 = step2Variables.get("lv3");
    assertEquals("Values for lv1", "[lfoo2]", lv1.getValues().toString());
    assertEquals("Values for lv2", "[lbar2]", lv2.getValues().toString());
    assertEquals("Values for lv3", "[llol2]", lv3.getValues().toString());
  }
}
