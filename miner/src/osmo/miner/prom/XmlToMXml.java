package osmo.miner.prom;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.miner.log.Logger;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Variable;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
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
  private final JFileChooser fc = new JFileChooser();
  private String description = "description";
  private String source = "source";
  private VelocityEngine velocity = new VelocityEngine();

  public XmlToMXml() throws Exception {

    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
      if ("Nimbus".equals(info.getName())) {
        UIManager.setLookAndFeel(info.getClassName());
        break;
      }
    }
    
    fc.setMultiSelectionEnabled(true);
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

    fc.showOpenDialog(null);
    File[] files = fc.getSelectedFiles();
    XmlProgramParser parser = new XmlProgramParser();
    Collection<Program> programs = new ArrayList<Program>();
    for (File file : files) {
      log.debug("Parsing file:"+file);
      programs.add(parser.parse(file));
    }
    write(programs);
  }

  public static void main(String[] args) throws Exception {
    Logger.debug = true;
    XmlToMXml m = new XmlToMXml();
  }

  public void write(Collection<Program> programs) throws Exception {
    VelocityContext vc = new VelocityContext();

    List<Variable> all = new ArrayList<Variable>();
    for (Program program : programs) {
      all.addAll(program.getGlobalVariables());
    }
    Collections.sort(all);
    vc.put("desc", description);
    vc.put("general_attrs", "");
    vc.put("src", source);
    vc.put("source_attrs", "");
    vc.put("process_attrs", createAttributes(all));
    vc.put("process_id", "process id");
    vc.put("process_desc", "process desc");

    String processes = createProgramStrings(programs);
    vc.put("processes", processes);

    StringWriter sw = new StringWriter();
    log.debug("Merging template");
    velocity.mergeTemplate("/osmo/miner/prom/WorkFlowLog.vm", "UTF8", vc, sw);
    File output = new File("osmominer-output.mxml");
    FileOutputStream out = new FileOutputStream(output);
    out.write(sw.toString().getBytes());
  }

  public String createProgramStrings(Collection<Program> programs) {
    StringWriter sw = new StringWriter();
    for (Program program : programs) {
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

    Map<String, Program> stepMap = program.getSteps();
    Collection<Program> steps = stepMap.values();
    log.debug("Steps:"+steps);
    StringWriter sw = new StringWriter();
    for (Program step : steps) {
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
