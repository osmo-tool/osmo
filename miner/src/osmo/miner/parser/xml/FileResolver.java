package osmo.miner.parser.xml;

import osmo.miner.model.program.Program;
import osmo.miner.parser.ProgramResolver;

import java.io.File;
import java.io.IOException;

/**
 * @author Teemu Kanstren
 */
public class FileResolver implements ProgramResolver {
  @Override
  public Program resolve(String reference) {
    XmlProgramParser parser = new XmlProgramParser();
    try {
      return parser.parse(new File(reference));
    } catch (IOException e) {
      throw new RuntimeException("Unable to open file with name:"+reference, e);
    }
  }
}
