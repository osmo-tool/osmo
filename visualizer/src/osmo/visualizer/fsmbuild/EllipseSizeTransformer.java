package osmo.visualizer.fsmbuild;

import com.google.common.base.Function;
import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class EllipseSizeTransformer implements Function<String, Integer> {

  @Override
  public Integer apply(String s) {
    return 100;
  }

}
