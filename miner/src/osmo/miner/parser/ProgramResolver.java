package osmo.miner.parser;

import osmo.miner.testminer.testcase.TestCase;

/**
 * @author Teemu Kanstren
 */
public interface ProgramResolver {
  public TestCase resolve(String reference);
}
