package osmo.tester.examples.helloworld;

import osmo.tester.model.dataflow.ValueSet;
import osmo.tester.scripting.dsm.*;

import java.util.Map;

/** @author Teemu Kanstren */
public class DSMLoaderMain {
  public static void main(String[] args) throws Exception {
    AsciiParser parser = new AsciiParser();
    DSMConfiguration config = parser.loadAndParse("osmo-dsl.txt");
    osmo.tester.scripting.dsm.DSMMain.execute(config);
  }
}
