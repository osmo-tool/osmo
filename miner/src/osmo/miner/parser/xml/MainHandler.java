package osmo.miner.parser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.parser.Miner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    List<ValuePair> pairs = new ArrayList<ValuePair>();
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.getQName(i);
      String value = attributes.getValue(i);
      pairs.add(new ValuePair(name, value));
    }
    for (Miner miner : miners) {
      miner.startElement(qName, pairs);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    for (Miner miner : miners) {
      miner.endElement(qName);
    }
  }
}
