package osmo.miner.parser;

import osmo.miner.model.program.Program;

/**
 * @author Teemu Kanstren
 */
public interface ProgramResolver {
  public Program resolve(String reference);
}
