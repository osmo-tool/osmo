package osmo.visualizer.model;

import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import osmo.tester.model.FSMTransition;

import java.awt.*;

/**
 * @author Teemu Kanstren
 */
public class TransitionVertextLabelRenderer extends DefaultVertexLabelRenderer {
  public TransitionVertextLabelRenderer(Color pickedVertexLabelColor) {
    super(pickedVertexLabelColor);
  }

  @Override
  protected void setValue(Object value) {
    FSMTransition t = (FSMTransition) value;
    super.setValue(t.getName());
  }
}
