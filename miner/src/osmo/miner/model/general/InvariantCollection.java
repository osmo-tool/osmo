package osmo.miner.model.general;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.miner.Config;
import osmo.miner.log.Logger;
import osmo.miner.model.dataflow.DataFlowInvariant;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static osmo.tester.TestUtils.getResource;

/**
 * @author Teemu Kanstren
 */
public class InvariantCollection {
  private static Logger log = new Logger(InvariantCollection.class);
  private Map<String, ScopeInvariants> scopes = new HashMap<String, ScopeInvariants>();
  private Collection<DataFlowInvariant> all = new ArrayList<DataFlowInvariant>();

  public Collection<DataFlowInvariant> getInvariants(String scope, String variable) {
    return scopes.get(scope).getInvariantsFor(variable);
  }

  public synchronized boolean addAll(Collection<? extends DataFlowInvariant> toAdd) {
    for (DataFlowInvariant invariant : toAdd) {
      add(invariant);
    }
    return toAdd.size() > 0;
  }

  public synchronized void add(DataFlowInvariant toAdd) {
    String scope = toAdd.getScope();
    ScopeInvariants scopeInvariants = scopes.get(scope);
    if (scopeInvariants == null) {
      scopeInvariants = new ScopeInvariants(scope);
      scopes.put(scope, scopeInvariants);
    }
    scopeInvariants.add(toAdd);
    all.add(toAdd);
  }

  public synchronized void addAll(InvariantCollection toAdd) {
    addAll(toAdd.all);
  }

  public int size() {
    return all.size();
  }

  @Override
  public String toString() {
    VelocityEngine velocity = Config.createVelocity();
    VelocityContext vc = new VelocityContext();
    vc.put("invariants", all);
    Class c = getClass();
    String templateName = c.getSimpleName() + ".vm";
    String template = getResource(c, templateName);
    StringWriter sw = new StringWriter();
    log.debug("Merging template:"+templateName);
    velocity.evaluate(vc, sw, templateName, template);
    return sw.toString();
  }
}
