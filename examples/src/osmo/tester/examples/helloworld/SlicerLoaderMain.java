package osmo.tester.examples.helloworld;

import osmo.tester.scripting.slicing.AsciiParser;
import osmo.tester.scripting.slicing.SlicingConfiguration;

/** @author Teemu Kanstren */
public class SlicerLoaderMain {
  public static void main(String[] args) throws Exception {
    AsciiParser parser = new AsciiParser();
    SlicingConfiguration config = parser.loadAndParse("osmo-dsl.txt");
    osmo.tester.scripting.slicing.SlicerMain.execute(config);
  }
}
