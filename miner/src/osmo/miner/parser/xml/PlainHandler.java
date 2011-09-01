package osmo.miner.parser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.miner.plain.PlainHierarchyMiner;
import osmo.miner.miner.program.ProgramModelMiner;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class PlainHandler extends DefaultHandler2 {
  private PlainHierarchyMiner plainMiner = new PlainHierarchyMiner();

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    Map<String, String> attrs = new HashMap<String, String>();
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.getQName(i);
      String value = attributes.getValue(i);
      attrs.put(name, value);
    }
    plainMiner.startElement(qName, attrs);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    plainMiner.endElement(qName);
  }

  public Node getRoot() {
    return plainMiner.getRoot();
  }
}
