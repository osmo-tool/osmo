package osmo.visualizer.fsmbuild;

import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class EllipseSizeTransformer implements Transformer<String, Integer> {
  @Override
  public Integer transform(String name) {
    return 100;
  }
}
