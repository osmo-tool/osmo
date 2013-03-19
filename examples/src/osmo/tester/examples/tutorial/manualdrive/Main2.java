package osmo.tester.examples.tutorial.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.scripting.manual.AsciiParser;
import osmo.tester.scripting.manual.ScripterMain;
import osmo.tester.scripting.manual.TestScript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class Main2 {
  public static void main(String[] args) throws Exception {
    OSMOConfiguration.setSeed(52);
    AsciiParser parser = new AsciiParser();
    List<TestScript> scripts = parser.loadAndParse("osmo-tests.txt");
    ScripterMain main = new ScripterMain();
    Collection<Object> models = new ArrayList<>();
    models.add(new HelloModel());
    main.run(models, scripts);
  }
}
