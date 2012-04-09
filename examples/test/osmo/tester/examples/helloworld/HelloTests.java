package osmo.tester.examples.helloworld;

import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.examples.helloworld.modular.ModularMain;
import osmo.tester.examples.helloworld.online.OnlineMain;
import osmo.tester.examples.helloworld.velocity.ScripterMain;
import osmo.tester.scripting.slicing.AsciiParser;
import osmo.tester.scripting.slicing.SlicingConfiguration;
import osmo.tester.scripting.slicing.SlicerMain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class HelloTests {
  private PrintStream sout = null;
  private ByteArrayOutputStream bos = null;
  
  @Before
  public void setup() {
    sout = System.out;
    bos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bos);
    System.setOut(out);
  }
  
  private void assertResults(String fileName) {
    System.setOut(sout);
    String expected = getResource(HelloTests.class, fileName);
    expected = unifyLineSeparators(expected, "\n");
    String actual = bos.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals(expected, actual);
  }
  
  @Test
  public void slicing() throws Exception {
    AsciiParser parser = new AsciiParser();
    String configText = TestUtils.getResource(HelloTests.class, "osmo-slice.txt");
    SlicingConfiguration config = parser.parse(configText);
    SlicerMain.execute(config);
    assertResults("expected-slicing-result.txt");
  }
  
  @Test
  public void velocity() {
    ScripterMain.main(null);
    assertResults("expected-velocity-result.txt");
  }
  
  @Test
  public void online() {
    OnlineMain.main(null);
    assertResults("expected-online-result.txt");
  }
  
  @Test
  public void modular() {
    ModularMain.main(null);
    assertResults("expected-modular-result.txt");
  }
}
