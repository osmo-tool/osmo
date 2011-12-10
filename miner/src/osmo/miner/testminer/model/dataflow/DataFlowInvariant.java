package osmo.miner.testminer.model.dataflow;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.log.Logger;
import osmo.miner.Config;

import java.io.StringWriter;

import static osmo.common.TestUtils.getResource;

/**
 * @author Teemu Kanstren
 */
public abstract class DataFlowInvariant {
  private static final Logger log = new Logger(DataFlowInvariant.class);
  protected final String scope;
  protected final String variable;
  private final boolean program;
  private final boolean global;
  protected boolean valid = true;

  protected DataFlowInvariant(String scope, String variable, boolean program, boolean global) {
    this.scope = scope;
    this.variable = variable;
    this.program = program;
    this.global = global;
  }

  public String getScope() {
    return scope;
  }

  public String getVariable() {
    return variable;
  }

  public boolean isValid() {
    return valid;
  }

  public boolean isProgram() {
    return program;
  }

  public boolean isGlobal() {
    return global;
  }

  public abstract void fill(VelocityContext vc);

  @Override
  public String toString() {
    VelocityEngine velocity = Config.createVelocity();
    VelocityContext vc = new VelocityContext();
    fill(vc);
    Class<? extends DataFlowInvariant> c = getClass();
    String templateName = c.getSimpleName() + ".vm";
    String template = getResource(c, templateName);
    StringWriter sw = new StringWriter();
//    log.debug("Merging template:"+templateName);
    //log.debugNL(".");
    velocity.evaluate(vc, sw, templateName, template);
    return sw.toString();
  }
}
