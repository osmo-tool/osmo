package osmo.visualizer.fsmbuild;

import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;

import java.awt.Shape;

/**
 * @author Teemu Kanstren
 */
public class EllipseVertexTransformer extends AbstractVertexShapeTransformer<String> implements Transformer<String, Shape> {
  public EllipseVertexTransformer() {
    setAspectRatioTransformer(new EllipseAspectRatioTransformer());
    setSizeTransformer(new EllipseSizeTransformer());
  }

  @Override
  public Shape transform(String name) {
    return factory.getEllipse(name);
  }
}
