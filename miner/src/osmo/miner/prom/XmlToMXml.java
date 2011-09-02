package osmo.miner.prom;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.miner.Config;
import osmo.miner.log.Logger;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;
import osmo.miner.model.program.Suite;
import osmo.miner.model.program.Variable;
import osmo.miner.parser.xml.XmlProgramParser;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    Program superProgram = suite.createEFSM();
    List<Variable> global = new ArrayList<Variable>();
    global.addAll(superProgram.getGlobalVariables());
    Collections.sort(global);
    List<Variable> top = new ArrayList<Variable>();
    top.addAll(superProgram.getVariables());
    Collections.sort(top);
    vc.put("desc", description);
    vc.put("general_attrs", "");
    vc.put("src", source);
    vc.put("source_attrs", createAttributes(global));
    vc.put("process_attrs", createAttributes(top));
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
    for (Program program : suite.getPrograms()) {
      sw.append(createProgramString(program));
    }
    return sw.toString();
  }

  public String createProgramString(Program main) {
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

  public String createMethodStrings(Program program) {
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

  public String createAttributes(List<Variable> variables) {
    Collections.sort(variables);
    StringWriter sw = new StringWriter();
    for (Variable variable : variables) {
      sw.append("      <Attribute name=\""+variable.getName()+"\">"+variable.getValues()+"</Attribute>\n");
    }
    return sw.toString();
  }
}
