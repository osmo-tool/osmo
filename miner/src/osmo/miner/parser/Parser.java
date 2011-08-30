package osmo.miner.parser;

import osmo.miner.miner.Miner;

import java.io.InputStream;

/**
 * @author Teemu Kanstren
 */
public interface Parser {
  public void parse(InputStream in);

  public void addMiner(Miner miner);
}
