package osmo.miner.parser.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import osmo.miner.Config;
import osmo.miner.log.Logger;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;
import osmo.miner.parser.ProgramResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ProgramHandler extends DefaultHandler2 {
  private static final Logger log = new Logger(ProgramHandler.class);
  private final Program program;
  private Step step;
  private ProgramResolver resolver = new FileResolver();

  public ProgramHandler(String name) {
    program = new Program(name);
    Config.validate();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    Map<String, String> attrs = new HashMap<String, String>();
    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.getQName(i);
      String value = attributes.getValue(i);
      attrs.put(name, value);
    }
    startElement(qName, attrs);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    endElement(qName);
  }

  public void startElement(String element, Map<String, String> attributes) {
    if (element.equals(Config.variableId)) {
      String name = attributes.get(Config.variableNameId);
      String value = attributes.get(Config.variableValueId);
      if (step != null) {
        step.addVariable(name, value);
      } else {
        program.addVariable(name, value);
      }
    }
    if (element.equals(Config.stepId)) {
      String name = attributes.get(Config.stepNameId);
//      log.debug("step start:"+name);
      step = program.createStep(name);
      Program sub = resolver.resolve(Config.baseDir+name);
      step.merge(sub);
    }
  }

  public void endElement(String element) {
    if (element.equals(Config.stepId)) {
      step = null;
//      log.debug("Ending step:"+ currentProgram.getName());
    }
  }

  public Program getProgram() {
    return program;
  }
}
