package osmo.miner.parser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.miner.Miner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class MainHandler extends DefaultHandler2 {
  private Collection<Miner> miners = new ArrayList<Miner>();

  public void addMiner(Miner miner) {
    miners.add(miner);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    Map<String, String> attrs = new HashMap<String, String>();
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.getQName(i);
      String value = attributes.getValue(i);
      attrs.put(name, value);
    }
    for (Miner miner : miners) {
      miner.startElement(qName, attrs);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    for (Miner miner : miners) {
      miner.endElement(qName);
    }
  }
}
