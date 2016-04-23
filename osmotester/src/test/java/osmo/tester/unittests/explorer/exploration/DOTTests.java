package osmo.tester.unittests.explorer.exploration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.explorer.trace.DOTWriter;
import osmo.tester.explorer.trace.TraceNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class DOTTests {
  @Before
  public void setup() {
    TraceNode.reset();
    DOTWriter.enabled = true;
    DOTWriter.deleteFiles();
  }

  @After
  public void down() {
    DOTWriter.enabled = false;
    DOTWriter.deleteFiles();
  }

  @Test
  public void writeDOT() throws Exception {
    TraceNode root = new TraceNode("root", null, false);
    TraceNode child = root.add("increase", false);
    child.add("decrease", true);
    TraceNode child2 = child.add("increase", false);
    child2.add("decrease", true);
    new DOTWriter(1).write(root, 2, "root");
    //only test the trace file and not the png picture as that is another process
    Path dotPath = Paths.get("osmo-dot/path1_1.dot");
    byte[] bytes = Files.readAllBytes(dotPath);
    String actual = new String(bytes, "UTF8");
    String expected = getResource(DOTTests.class, "expected-dot.txt");
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals("Trace output", expected, actual);
  }
}
