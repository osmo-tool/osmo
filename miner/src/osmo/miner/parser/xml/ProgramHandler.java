package osmo.miner.parser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import osmo.miner.miner.ProgramMiner;
import osmo.miner.model.program.Program;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ProgramHandler extends DefaultHandler2 {
  private ProgramMiner programMiner;

  public ProgramHandler(String name) {
    programMiner = new ProgramMiner(name);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    Map<String, String> attrs = new HashMap<String, String>();
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.getQName(i);
      String value = attributes.getValue(i);
      attrs.put(name, value);
    }
    programMiner.startElement(qName, attrs);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    programMiner.endElement(qName);
  }

  public Program getProgram() {
    return programMiner.getProgram();
  }
}
