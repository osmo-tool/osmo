package osmo.miner.prom;

import org.xml.sax.ext.DefaultHandler2;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Teemu Kanstren
 */
public class XmlProgramParser {
  public Program parse(File file) throws Exception {
    return parse(new FileInputStream(file), file.getName());
  }

  public Program parse(InputStream in, String name) throws Exception {
    ProgramHandler handler = new ProgramHandler(name);
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    try {
      SAXParser parser = factory.newSAXParser();
      parser.parse(in, handler);
      in.close();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create SAX (XML) parser.", e);
    }
    return handler.getProgram();
  }
}
