package osmo.miner.prom;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.log.Logger;
import osmo.miner.Config;
import osmo.miner.testminer.testcase.TestCase;
import osmo.miner.testminer.testcase.Step;
import osmo.miner.testminer.testcase.Suite;
import osmo.miner.parser.xml.XmlProgramParser;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class XmlToMXml {
  private static final Logger log = new Logger(XmlToMXml.class);
  private String description = "description";
  private String source = "source";
  private VelocityEngine velocity = Config.createVelocity();

  public XmlToMXml() {
  }

  public void chooseAndWrite() throws IOException {
    Config.validate();
    JFileChooser fc = new JFileChooser();
    fc.setMultiSelectionEnabled(true);

    fc.showOpenDialog(null);
    File[] files = fc.getSelectedFiles();
    XmlProgramParser parser = new XmlProgramParser();
    Suite suite = new Suite();
    for (File file : files) {
      log.debug("Parsing file:"+file);
      suite.add(parser.parse(file));
    }
    File output = new File("osmominer-output.mxml");
    FileOutputStream out = new FileOutputStream(output);
    write(suite, out);
  }

  public void write(Suite suite, OutputStream out) throws IOException {
    VelocityContext vc = new VelocityContext();

    vc.put("desc", description);
    vc.put("general_attrs", "");
    vc.put("src", source);
    vc.put("source_attrs", "");
    vc.put("process_attrs", "");
    vc.put("process_id", "process id");
    vc.put("process_desc", "process desc");

    String processes = createProgramStrings(suite);
    vc.put("processes", processes);

    StringWriter sw = new StringWriter();
    log.debug("Merging template");
    velocity.mergeTemplate("/osmo/miner/prom/WorkFlowLog.vm", "UTF8", vc, sw);
    out.write(sw.toString().getBytes());
  }

  public String createProgramStrings(Suite suite) {
    StringWriter sw = new StringWriter();
    for (TestCase program : suite.getTests()) {
      sw.append(createProgramString(program));
    }
    return sw.toString();
  }

  public String createProgramString(TestCase main) {
    VelocityContext vc = new VelocityContext();

    String entries = createMethodStrings(main);

    vc.put("id", main.getName());
    vc.put("desc", "program desc");
    vc.put("attrs", createAttributes(main.getVariables()));
    vc.put("entries", entries);

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("/osmo/miner/prom/process.vm", "UTF8", vc, sw);
    return sw.toString();
  }

  public String createMethodStrings(TestCase program) {
    VelocityContext vc = new VelocityContext();

    Collection<Step> steps = program.getSteps();
    log.debug("Steps:"+steps);
    StringWriter sw = new StringWriter();
    for (Step step : steps) {
      vc.put("method", step.getName());
      vc.put("params", createAttributes(step.getVariables()));
      //time is not used but is possible
      vc.put("orig", "");
      velocity.mergeTemplate("/osmo/miner/prom/entry.vm", "UTF8", vc, sw);
    }
    return sw.toString();
  }

  public String createAttributes(Map<String, String> variables) {
    List<String> names = new ArrayList<String>();
    names.addAll(variables.keySet());
    Collections.sort(names);
    StringWriter sw = new StringWriter();
    for (String name : names) {
      sw.append("      <Attribute name=\""+name+"\">"+variables.get(name)+"</Attribute>\n");
    }
    return sw.toString();
  }
}
