package osmo.miner.gui.mainform.programmodel;

import osmo.miner.gui.mainform.ModelObject;
import osmo.miner.log.Logger;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;
import osmo.miner.parser.xml.ProgramHandler;
import osmo.miner.parser.xml.XmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ProgramParser {
  private static final Logger log = new Logger(ProgramParser.class);
  private final Map<ModelObject, Program> programs = new HashMap<ModelObject, Program>();
  private final Map<ModelObject, Node> roots = new HashMap<ModelObject, Node>();

  public synchronized Node nodeFor(ModelObject mo) {
    Node root = roots.get(mo);
    if (root == null) {
      parse(mo);
      root = roots.get(mo);
    }
    return root;
  }

  public synchronized Program programFor(ModelObject mo) {
    Program program = programs.get(mo);
    if (program == null) {
      parse(mo);
      program = programs.get(mo);
    }
    return program;
  }

  private void parse(ModelObject mo) {
    ProgramHandler handler = new ProgramHandler();
    XmlParser parser = new XmlParser(handler);
    InputStream inputStream = mo.getInputStream();
    parser.parse(inputStream);
    try {
      inputStream.close();
    } catch (IOException e) {
      log.error("Failed to close inputstream", e);
    }
    Node root = handler.getRoot();
    roots.put(mo, root);
    programs.put(mo, handler.getProgram());
  }
}
