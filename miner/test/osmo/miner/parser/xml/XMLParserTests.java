package osmo.miner.parser.xml;

import org.junit.Test;
import osmo.miner.miner.Miner;
import osmo.miner.miner.plain.PlainHierarchyMiner;
import osmo.miner.model.Node;

import java.io.InputStream;

import static junit.framework.Assert.*;
import static osmo.tester.TestUtils.*;

public class XMLParserTests {
  @Test
  public void parseXML() {
    InputStream file1 = getClass().getResourceAsStream("testfile1.xml");
    XmlParser parser = new XmlParser();
    Miner miner = new PlainHierarchyMiner();
    parser.addMiner(miner);
    parser.parse(file1);
    Node root = miner.getRoot();
    String expected = getResource(getClass(), "expected1.txt");
    assertEquals("Parsed from XML", expected, root.treeString());
  }
}
