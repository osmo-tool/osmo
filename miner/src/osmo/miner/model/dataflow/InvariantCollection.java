package osmo.miner.model.dataflow;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.miner.Config;
import osmo.miner.log.Logger;

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
  private Map<String, Collection<DataFlowInvariant>> scopeVariableMap = new HashMap<String, Collection<DataFlowInvariant>>();
  private Collection<DataFlowInvariant> all = new ArrayList<DataFlowInvariant>();

  public Collection<DataFlowInvariant> getInvariants(String scope, String variable) {
    String key = scope+":"+variable;
    return scopeVariableMap.get(key);
  }

  public synchronized boolean addAll(Collection<? extends DataFlowInvariant> toAdd) {
    for (DataFlowInvariant invariant : toAdd) {
      String key = invariant.getScope() + ":" + invariant.getVariable();
      Collection<DataFlowInvariant> current = scopeVariableMap.get(key);
      if (current == null) {
        current = new ArrayList<DataFlowInvariant>();
        scopeVariableMap.put(key, current);
      }
      current.add(invariant);
    }
    all.addAll(toAdd);
    return toAdd.size() > 0;
  }

  public synchronized void addAll(InvariantCollection toAdd) {
    Set<String> keys = toAdd.scopeVariableMap.keySet();
    for (String key : keys) {
      for (DataFlowInvariant invariant : toAdd.scopeVariableMap.get(key)) {
        Collection<DataFlowInvariant> current = scopeVariableMap.get(key);
        if (current == null) {
          current = new ArrayList<DataFlowInvariant>();
          scopeVariableMap.put(key, current);
        }
        current.add(invariant);
      }
    }
    all.addAll(toAdd.all);
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
