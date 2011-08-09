package osmo.visualizer.model;

import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class EllipseAspectRatioTransformer implements Transformer<FSMTransition,Float> {
  @Override
  public Float transform(FSMTransition transition) {
    return 0.5f;
  }
}
