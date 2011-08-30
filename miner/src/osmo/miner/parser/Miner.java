package osmo.miner.parser;

import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.model.Node;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public interface Miner {
  public void startElement(String name, List<ValuePair> attributes);

  public void endElement(String name);

  public Node getRoot();
}
