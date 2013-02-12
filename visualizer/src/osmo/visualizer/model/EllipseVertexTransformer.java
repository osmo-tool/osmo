package osmo.visualizer.model;

import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import org.apache.commons.collections15.Transformer;
import osmo.tester.model.FSMTransition;

import java.awt.Shape;

/**
 * @author Teemu Kanstren
 */
public class EllipseVertexTransformer extends AbstractVertexShapeTransformer<FSMTransition> implements Transformer<FSMTransition, Shape> {
  public EllipseVertexTransformer() {
    setAspectRatioTransformer(new EllipseAspectRatioTransformer());
    setSizeTransformer(new EllipseSizeTransformer());
  }

  @Override
  public Shape transform(FSMTransition transition) {
    return factory.getEllipse(transition);
  }
}
