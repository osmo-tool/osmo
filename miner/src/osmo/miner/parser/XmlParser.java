package osmo.miner.parser;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import osmo.miner.model.HierarchyModel;
import osmo.miner.model.Node;

public class XmlParser {
  private final SAXParser parser;

  public XmlParser() {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    try {
      parser = factory.newSAXParser();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create SAX (XML) parser.", e);
    }
  }

  public HierarchyModel parse(Node root, InputStream in) {
    HierarchyHandler handler = new HierarchyHandler(root);
    try {
      parser.parse(in, handler);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse given InputStream.", e);
    }
    return handler.getModel();
  }
}
