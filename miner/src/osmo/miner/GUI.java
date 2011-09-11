package osmo.miner;

import osmo.miner.log.Logger;
import osmo.miner.miner.MainMiner;
import osmo.miner.miner.dataflow.ValueRangeMiner;
import osmo.miner.miner.dataflow.ValueSetMiner;
import osmo.miner.model.program.Suite;
import osmo.miner.parser.xml.XmlProgramParser;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Teemu Kanstren
 */
public class GUI {
  private static Logger log = new Logger(GUI.class);

  public void choose() throws Exception {
    Config.validate();
    JFileChooser fc = new JFileChooser();
    fc.setMultiSelectionEnabled(true);

    fc.showOpenDialog(null);
    File[] files = fc.getSelectedFiles();
    XmlProgramParser parser = new XmlProgramParser();
    Suite suite = new Suite();
    for (File file : files) {
      log.debug("Parsing file:" + file);
      suite.add(parser.parse(file));
    }
    MainMiner miner = new MainMiner();
    ValueRangeMiner vrMiner = new ValueRangeMiner();
    miner.addMiner(vrMiner);
    miner.addMiner(new ValueSetMiner());
    miner.mine(suite);
    File output = new File("osmominer-output.txt");
    FileOutputStream out = new FileOutputStream(output);
    String info = miner.getInvariants().toString();
    out.write(info.getBytes());
  }
}
