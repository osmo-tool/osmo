package osmo.visualizer.fsmbuild;

import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class EllipseAspectRatioTransformer implements Transformer<String,Float> {
  @Override
  public Float transform(String name) {
    return 0.5f;
  }
}
