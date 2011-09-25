package osmo.miner.model.dataflow;

import org.apache.velocity.VelocityContext;
import osmo.common.ValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ValueSet extends DataFlowInvariant {
  private Map<String, Integer> observations = new HashMap<String, Integer>();

  public ValueSet(String scope, String variable, boolean program, boolean global) {
    super(scope, variable, program, global);
  }

  public ValueSet(String scope, String variable) {
    super(scope, variable, false, false);
  }

  public void add(String option) {
    Integer count = observations.get(option);
    if (count == null) {
      count = 0;
    }
    observations.put(option, count+1);
  }

  @Override
  public void fill(VelocityContext vc) {
    Collection<ValuePair<String>> values = new ArrayList<ValuePair<String>>();
    for (String key : observations.keySet()) {
      int count = observations.get(key);
      values.add(new ValuePair<String>(key, ""+count));
    }
    vc.put("values", values);
  }
}
