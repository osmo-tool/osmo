package osmo.visualizer.model;

import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class EllipseSizeTransformer implements Transformer<FSMTransition, Integer> {
  @Override
  public Integer transform(FSMTransition transition) {
    return 100;
  }
}
