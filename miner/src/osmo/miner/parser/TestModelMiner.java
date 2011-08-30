package osmo.miner.parser;

import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.model.Node;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class TestModelMiner implements Miner {
  @Override
  public void startElement(String name, List<ValuePair> attributes) {
  }

  @Override
  public void endElement(String name) {
  }

  @Override
  public Node getRoot() {
    return null;
  }
}
