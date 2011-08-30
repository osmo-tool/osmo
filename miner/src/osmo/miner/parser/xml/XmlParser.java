package osmo.miner.parser.xml;

import osmo.miner.miner.Miner;
import osmo.miner.parser.Parser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

public class XmlParser implements Parser {
  private final SAXParser parser;
  private MainHandler handler = new MainHandler();

  public XmlParser() {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    try {
      parser = factory.newSAXParser();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create SAX (XML) parser.", e);
    }
  }

  public void parse(InputStream in) {
    try {
      parser.parse(in, handler);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse given InputStream.", e);
    }
  }

  public void addMiner(Miner miner) {
    handler.addMiner(miner);
  }
}
