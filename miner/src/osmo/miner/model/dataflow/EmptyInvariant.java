package osmo.miner.model.dataflow;

import org.apache.velocity.VelocityContext;

/**
 * @author Teemu Kanstren
 */
public class EmptyInvariant extends DataFlowInvariant {
  public EmptyInvariant(String scope, String variable) {
    super(scope, variable);
  }

  @Override
  public void fill(VelocityContext vc) {
  }
}
