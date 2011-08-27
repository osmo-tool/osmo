package osmo.miner.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import osmo.miner.model.HierarchyModel;
import osmo.miner.model.Node;

public class HierarchyHandler extends DefaultHandler2 {
  private final HierarchyModel model;

  public HierarchyHandler(Node root) {
    model = new HierarchyModel(root);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {

  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    model.add(qName);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    model.up();
  }

  public HierarchyModel getModel() {
    return model;
  }

}
