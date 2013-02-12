package osmo.tester.examples.tutorial.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.scripting.slicing.AsciiParser;
import osmo.tester.scripting.slicing.SlicerMain;
import osmo.tester.scripting.slicing.SlicingConfiguration;

/** @author Teemu Kanstren */
public class Main4 {
  public static void main(String[] args) throws Exception {
    OSMOConfiguration.setSeed(52);
    AsciiParser parser = new AsciiParser();
    SlicingConfiguration config = parser.loadAndParse("osmo-dsl.txt");
    SlicerMain.execute(config);
  }
}
