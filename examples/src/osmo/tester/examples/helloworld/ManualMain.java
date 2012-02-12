package osmo.tester.examples.helloworld;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.tester.scripting.manual.AsciiParser;
import osmo.tester.scripting.manual.ScripterMain;
import osmo.tester.scripting.manual.TestScript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class ManualMain {
  public static void main(String[] args) throws Exception {
    AsciiParser parser = new AsciiParser();
    List<TestScript> scripts = parser.loadAndParse("osmo-tests.txt");
    ScripterMain main = new ScripterMain();
    main.setSeed(345);
    Collection<Object> models = new ArrayList<>();
    models.add(new HelloModel());
    main.run(models, scripts);
  }
}
