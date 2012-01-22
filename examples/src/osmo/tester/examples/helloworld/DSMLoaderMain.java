package osmo.tester.examples.helloworld;

import osmo.tester.scripting.dsm.*;

/** @author Teemu Kanstren */
public class DSMLoaderMain {
  public static void main(String[] args) throws Exception {
    AsciiParser parser = new AsciiParser();
    DSMConfiguration config = parser.loadAndParse("osmo-dsm.txt");
    osmo.tester.scripting.dsm.DSMMain.execute(config);
  }
}
