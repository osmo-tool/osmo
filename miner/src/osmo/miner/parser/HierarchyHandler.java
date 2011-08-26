package osmo.miner.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import osmo.miner.model.HierarchyModel;

public class HierarchyHandler extends DefaultHandler2 {
  private int depth = 0;
  private final HierarchyModel model = new HierarchyModel();

  public HierarchyHandler() {
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {

  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    depth++;
    model.add(qName);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    depth--;
    model.up();
  }

  public HierarchyModel getModel() {
    return model;
  }

}
