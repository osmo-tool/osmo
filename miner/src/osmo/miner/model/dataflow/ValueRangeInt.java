package osmo.miner.model.dataflow;

import org.apache.velocity.VelocityContext;

/**
 * @author Teemu Kanstren
 */
public class ValueRangeInt extends DataFlowInvariant {
  private int min = Integer.MAX_VALUE;
  private int max = Integer.MIN_VALUE;

  public ValueRangeInt(String scope, String variable, boolean program, boolean global) {
    super(scope, variable, program, global);
  }

  public ValueRangeInt(String scope, String variable) {
    super(scope, variable, false, false);
  }

  public void check(String value) {
    try {
      int i = Integer.parseInt(value);
      if (i < min) {
        min = i;
      }
      if (i > max) {
        max = i;
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
  }

  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  @Override
  public void fill(VelocityContext vc) {
    vc.put("var", variable);
    vc.put("scope", scope);
    vc.put("min", min);
    vc.put("max", max);
    vc.put("valid", valid);
  }

  @Override
  public String toString() {
    if (!valid) {
      return "Range: Non-numeric (invalid)";
    }
    return "Range: "+min+"-"+max;
  }
}
