package osmo.miner.testminer.model.general;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.log.Logger;
import osmo.miner.Config;
import osmo.miner.testminer.model.dataflow.DataFlowInvariant;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static osmo.common.TestUtils.getResource;

/**
 * @author Teemu Kanstren
 */
public class InvariantCollection {
  private static Logger log = new Logger(InvariantCollection.class);
  private Map<String, ScopeVariables> scopes = new HashMap<String, ScopeVariables>();
  private Collection<DataFlowInvariant> all = new ArrayList<DataFlowInvariant>();

  public VariableInvariants getInvariants(String scope, String variable) {
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
    ScopeVariables scopeVariables = scopes.get(scope);
    if (scopeVariables == null) {
      scopeVariables = new ScopeVariables(scope, toAdd.isProgram(), toAdd.isGlobal());
      scopes.put(scope, scopeVariables);
    }
    scopeVariables.add(toAdd);
    all.add(toAdd);
  }

  public synchronized void addAll(InvariantCollection toAdd) {
    addAll(toAdd.all);
  }

  public int size() {
    return all.size();
  }

  public Collection<ScopeVariables> getSortedScopes() {
    List<ScopeVariables> result = new ArrayList<ScopeVariables>();
    List<ScopeVariables> globalScope = new ArrayList<ScopeVariables>();
    List<ScopeVariables> programScope = new ArrayList<ScopeVariables>();
    for (ScopeVariables scope : scopes.values()) {
      if (scope.isGlobal()) {
        globalScope.add(scope);
        continue;
      }
      if (scope.isProgram()) {
        programScope.add(scope);
        continue;
      }
      result.add(scope);
    }
    result.addAll(0, programScope);
    result.addAll(0, globalScope);
    return result;
  }

  @Override
  public String toString() {
    VelocityEngine velocity = Config.createVelocity();
    VelocityContext vc = new VelocityContext();
    vc.put("scopes", getSortedScopes());
    vc.put("program", "TestProgram");
    Class c = getClass();
    String templateName = c.getSimpleName() + ".vm";
    String template = getResource(c, templateName);
    StringWriter sw = new StringWriter();
    log.debug("Merging template:"+templateName);
    velocity.evaluate(vc, sw, templateName, template);
    return sw.toString();
  }
}
