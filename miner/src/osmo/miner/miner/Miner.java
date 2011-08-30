package osmo.miner.miner;

import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.model.Node;

import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public interface Miner {
  public void startElement(String name, Map<String, String> attributes);

  public void endElement(String name);

  public Node getRoot();
}
