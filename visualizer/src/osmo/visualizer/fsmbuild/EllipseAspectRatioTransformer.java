package osmo.visualizer.fsmbuild;

import com.google.common.base.Function;
import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;


/**
 * @author Teemu Kanstren
 */
public class EllipseAspectRatioTransformer implements Function<String,Float> {
  @Override
  public Float apply(String s) {
    return 0.5f;
  }
}
