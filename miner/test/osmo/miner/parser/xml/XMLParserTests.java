package osmo.miner.parser.xml;

import java.io.InputStream;

import org.junit.Test;

import osmo.miner.model.Node;
import osmo.miner.parser.XmlParser;

public class XMLParserTests {
  @Test
  public void parseXML() {
    InputStream file1 = getClass().getResourceAsStream("testfile1.xml");
    XmlParser parser = new XmlParser();
    Node root = parser.parse(file1);
    System.out.println("model:\n" + root);
  }
}
