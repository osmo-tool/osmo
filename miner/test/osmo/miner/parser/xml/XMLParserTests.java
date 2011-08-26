package osmo.miner.parser.xml;

import java.io.InputStream;

import org.junit.Test;

import osmo.miner.model.HierarchyModel;
import osmo.miner.parser.XmlParser;

public class XMLParserTests {
  @Test
  public void parseXML() {
    InputStream file1 = getClass().getResourceAsStream("testfile1.xml");
    InputStream file2 = getClass().getResourceAsStream("testfile1.xml");
    XmlParser parser = new XmlParser();
    HierarchyModel model1 = parser.parse(file1);
    HierarchyModel model2 = parser.parse(file2);
    System.out.println("model:\n" + model1);
  }
}
