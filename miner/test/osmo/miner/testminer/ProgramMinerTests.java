package osmo.miner.testminer;

import org.junit.Before;
import org.junit.Test;
import osmo.miner.Config;
import osmo.miner.testminer.testcase.TestCase;
import osmo.miner.testminer.testcase.Step;
import osmo.miner.parser.xml.XmlProgramParser;

import java.io.InputStream;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Teemu Kanstren
 */
public class ProgramMinerTests {
  private XmlProgramParser parser;

  @Before
  public void setup() {
//    Logger.debug = true;
    Config.variableId = "variable";
    Config.variableNameId = "name";
    Config.variableValueId = "value";
    Config.stepId = "teststep";
    Config.stepNameId = "name";
    parser = new XmlProgramParser();
  }

  @Test
  public void singleStepVariables() throws Exception {
    InputStream file1 = getClass().getResourceAsStream("testmodel1.xml");
    TestCase program = parser.parse(file1, "SingleStepVariablesTest");
    Map<String, Step> steps = program.getStepMap();
    Map<String, String> variables = program.getVariables();
    assertEquals("Number of parsed variables", 2, variables.size());
    String gv1 = variables.get("gv1");
    String gv2 = variables.get("gv2");
    String lv1 = variables.get("lv1");
    String lv2 = variables.get("lv2");
    assertEquals("Value for gv1", "gfoo", gv1);
    assertEquals("Value for gv2", "gbar", gv2);
    assertEquals("Value for lv1", null, lv1);
    assertEquals("Value for lv2", null, lv2);

    Step step1 = steps.get("step1");
    Map<String, String> step1Variables = step1.getVariables();
    lv1 = step1Variables.get("lv1");
    lv2 = step1Variables.get("lv2");
    assertEquals("Value for lv1", "lfoo", lv1);
    assertEquals("Value for lv2", "lbar", lv2);
  }

  @Test
  public void twoStepVariables() throws Exception {
    InputStream file1 = getClass().getResourceAsStream("testmodel2.xml");
    TestCase program = parser.parse(file1, "TwoStepVariablesTest");
    Map<String, Step> steps = program.getStepMap();
    Map<String, String> variables = program.getVariables();
    assertEquals("Number of parsed variables", 2, variables.size());
    String gv1 = variables.get("gv1");
    String gv2 = variables.get("gv2");
    String lv1 = variables.get("lv1");
    String lv2 = variables.get("lv2");
    assertEquals("Value for gv1", "gfoo", gv1);
    assertEquals("Value for gv2", "gbar", gv2);
    assertEquals("Value for lv1", null, lv1);
    assertEquals("Value for lv2", null, lv2);

    Step step1 = steps.get("step1");
    Map<String, String> step1Variables = step1.getVariables();
    lv1 = step1Variables.get("lv1");
    lv2 = step1Variables.get("lv2");
    assertEquals("Value for lv1", "lfoo1", lv1);
    assertEquals("Value for lv2", "lbar1", lv2);

    Step step2 = steps.get("step2");
    Map<String, String> step2Variables = step2.getVariables();
    lv1 = step2Variables.get("lv1");
    lv2 = step2Variables.get("lv2");
    String lv3 = step2Variables.get("lv3");
    assertEquals("Value for lv1", "lfoo2", lv1);
    assertEquals("Value for lv2", "lbar2", lv2);
    assertEquals("Value for lv3", "llol2", lv3);
  }

  @Test
  public void twoModelsWithOverlap() throws Exception {
    InputStream file1 = getClass().getResourceAsStream("testmodel1.xml");
    InputStream file2 = getClass().getResourceAsStream("testmodel2.xml");
    parser.parse(file1, "Test1");
    TestCase program = parser.parse(file2, "Test2");

    Map<String, Step> steps = program.getStepMap();
    Map<String, String> variables = program.getVariables();
    assertEquals("Number of parsed variables", 2, variables.size());
    String gv1 = variables.get("gv1");
    String gv2 = variables.get("gv2");
    String lv1 = variables.get("lv1");
    String lv2 = variables.get("lv2");
    assertEquals("Value for gv1", "gfoo", gv1);
    assertEquals("Value for gv2", "gbar", gv2);
    assertEquals("Value for lv1", null, lv1);
    assertEquals("Value for lv2", null, lv2);

    Step step1 = steps.get("step1");
    Map<String, String> step1Variables = step1.getVariables();
    lv1 = step1Variables.get("lv1");
    lv2 = step1Variables.get("lv2");
    assertEquals("Value for lv1", "lfoo1", lv1);
    assertEquals("Value for lv2", "lbar1", lv2);

    Step step2 = steps.get("step2");
    Map<String, String> step2Variables = step2.getVariables();
    lv1 = step2Variables.get("lv1");
    lv2 = step2Variables.get("lv2");
    String lv3 = step2Variables.get("lv3");
    assertEquals("Value for lv1", "lfoo2", lv1);
    assertEquals("Value for lv2", "lbar2", lv2);
    assertEquals("Value for lv3", "llol2", lv3);
  }
}
