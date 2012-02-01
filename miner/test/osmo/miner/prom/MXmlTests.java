package osmo.miner.prom;

import org.junit.Test;
import osmo.miner.testminer.testcase.Suite;
import osmo.miner.testmodels.TestModels1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class MXmlTests {
  @Test
  public void model1() throws IOException {
    //Logger.debug = true;
    Suite programs = TestModels1.model1();
    XmlToMXml transformer = new XmlToMXml();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    transformer.write(programs, out);
    String actual = out.toString();
    actual = unifyLineSeparators(actual, "\n");
    String expected = getResource(getClass(), "expected-output-1.txt");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Generated MXML", expected, actual);
  }
}
