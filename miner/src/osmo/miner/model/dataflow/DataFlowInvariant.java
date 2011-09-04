package osmo.miner.model.dataflow;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.miner.Config;
import osmo.miner.log.Logger;

import java.io.StringWriter;

import static osmo.tester.TestUtils.getResource;

/**
 * @author Teemu Kanstren
 */
public abstract class DataFlowInvariant {
  private static final Logger log = new Logger(DataFlowInvariant.class);
  protected final String scope;
  protected final String variable;
  protected boolean valid = true;

  protected DataFlowInvariant(String scope, String variable) {
    this.scope = scope;
    this.variable = variable;
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
    log.debug("Merging template:"+templateName);
    velocity.evaluate(vc, sw, templateName, template);
    return sw.toString();
  }
}
