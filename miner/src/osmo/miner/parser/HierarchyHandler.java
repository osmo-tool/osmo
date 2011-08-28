package osmo.miner.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.model.Node;

import java.util.ArrayList;
import java.util.List;

public class HierarchyHandler extends DefaultHandler2 {
  private Node root = new Node(null, "root", new ArrayList<ValuePair>());
  private Node current = null;

  public HierarchyHandler() {
    current = root;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {

  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    List<ValuePair> pairs = new ArrayList<ValuePair>();
    for (int i = 0 ; i < attributes.getLength() ; i++) {
      String name = attributes.getQName(i);
      String value = attributes.getValue(i);
      pairs.add(new ValuePair(name, value));
    }
    current = current.addChild(qName, pairs);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    current = current.getParent();
  }

  public Node getRoot() {
    return root;
  }
}
