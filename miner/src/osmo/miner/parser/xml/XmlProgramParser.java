package osmo.miner.parser.xml;

import osmo.miner.model.program.Program;
import osmo.miner.parser.StartEndVariableResolver;

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
  public Program parse(File file) throws IOException {
    FileInputStream in = new FileInputStream(file);
    Program program = parse(in, file.getName());
    StartEndVariableResolver vr = new StartEndVariableResolver();
    vr.resolve(program);
    return program;
  }

  public Program parse(InputStream in, String name) {
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
    Program program = handler.getProgram();
    return program;
  }
}
