package osmo.miner.parser.xml;

import org.xml.sax.ext.DefaultHandler2;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

public class XmlParser {
  private final SAXParser parser;
  private final DefaultHandler2 handler;

  public XmlParser(DefaultHandler2 handler) {
    this.handler = handler;
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
}
