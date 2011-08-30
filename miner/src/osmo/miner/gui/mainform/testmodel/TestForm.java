package osmo.miner.gui.mainform.testmodel;

import osmo.miner.gui.TreeForm;
import osmo.miner.parser.Miner;
import osmo.miner.parser.Parser;
import osmo.miner.parser.TestModelMiner;
import osmo.miner.parser.xml.XmlParser;

/**
 * @author Teemu Kanstren
 */
public class TestForm extends TreeForm {
  public TestForm() {
  }

  @Override
  public Parser createParser() {
    return new XmlParser();
  }

  @Override
  public Miner createMiner() {
    return new TestModelMiner();
  }
}
