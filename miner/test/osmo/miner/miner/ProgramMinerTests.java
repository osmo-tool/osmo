package osmo.miner.miner;

import org.junit.Test;
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
  @Test
  public void globalVariables() {
    InputStream file1 = getClass().getResourceAsStream("testmodel1.xml");
    XmlParser parser = new XmlParser();
    ProgramModelMiner miner = new ProgramModelMiner();
    parser.addMiner(miner);
    parser.parse(file1);
    Program program = miner.getProgram();
    Map<String, Variable> variables = program.getVariables();
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


}
