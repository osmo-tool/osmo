package osmo.miner.gui.mainform.plainmodel;

import osmo.miner.gui.TreeForm;
import osmo.miner.parser.Miner;
import osmo.miner.parser.Parser;
import osmo.miner.parser.PlainHierarchyMiner;
import osmo.miner.parser.xml.XmlParser;

/**
 * @author Teemu Kanstren
 */
public class PMForm extends TreeForm {
  public PMForm() {
  }

  @Override
  public Parser createParser() {
    return new XmlParser();
  }

  @Override
  public Miner createMiner() {
    return new PlainHierarchyMiner();
  }
}
