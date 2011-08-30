package osmo.miner.parser.xml;

import org.junit.Test;
import osmo.miner.model.Node;
import osmo.miner.parser.Miner;
import osmo.miner.parser.PlainHierarchyMiner;

import java.io.InputStream;

public class XMLParserTests {
  @Test
  public void parseXML() {
    InputStream file1 = getClass().getResourceAsStream("testfile1.xml");
    XmlParser parser = new XmlParser();
    Miner miner = new PlainHierarchyMiner();
    parser.addMiner(miner);
    parser.parse(file1);
    Node root = miner.getRoot();
    System.out.println("model:\n" + root.treeString());
  }
}
