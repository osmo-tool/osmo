package osmo.visualizer.model;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import osmo.tester.OSMOTester;
import osmo.tester.examples.CalculatorModel;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Teemu Kanstren
 */
public class FSMBuildVisualizer extends JFrame implements GenerationListener {

  private Graph<FSMTransition, String> graph;
  private Set<String> edges = new HashSet<String>();
  private Set<FSMTransition> vertices = new HashSet<FSMTransition>();
  private FSMTransition current = new FSMTransition("init");
  private int index = 0;
  private VisualizationViewer<FSMTransition, String> vv;

  public FSMBuildVisualizer() {
    super("Model Visualizer");
    graph = new SparseMultigraph<FSMTransition, String>();
    graph.addVertex(current);
//    Layout<FSMTransition, String> layout = new CircleLayout<FSMTransition, String>(graph);
    Layout<FSMTransition, String> layout = new FRLayout<FSMTransition, String>(graph);
    layout.setSize(new Dimension(800, 600)); // sets the initial size of the space
    vv = new VisualizationViewer<FSMTransition, String>(layout);
    vv.setPreferredSize(new Dimension(800, 600)); //Sets the viewing area size
    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
    VertexLabelAsShapeRenderer<FSMTransition, String> vlasr = new VertexLabelAsShapeRenderer<FSMTransition, String>(vv.getRenderContext());
//    vv.getRenderContext().setVertexShapeTransformer(vlasr);
    vv.getRenderContext().setVertexShapeTransformer(new EllipseVertexTransformer());
//    vv.getRenderContext().setVertexLabelRenderer(new TransitionVertextLabelRenderer(Color.GREEN));
    DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
    gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    vv.setGraphMouse(gm);
    getContentPane().add(vv);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1024, 768);
    pack();
    setVisible(true);
  }

  @Override
  public void guard(FSMTransition t) {
    String edge = current.getName() + "->" + t.getName();
    if (!edges.contains(edge)) {
      System.out.println("EDGE+" + edge);
      if (!vertices.contains(t)) {
        System.out.println("VERTEX+" + t);
        graph.addVertex(t);
        vertices.add(t);
      }
      edges.add(edge);
      graph.addEdge("" + index, current, t);
      index++;
      vv.repaint();
    }
  }

  @Override
  public void transition(FSMTransition t) {
    current = t;
  }

  @Override
  public void pre(FSMTransition t) {
  }

  @Override
  public void post(FSMTransition t) {
  }

  @Override
  public void testStarted(TestCase test) {
  }

  @Override
  public void testEnded(TestCase test) {
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }

  public static void main(String[] args) {
/*
    Graph<FSMTransition, String> g = new SparseMultigraph<FSMTransition, String>();
    FSMTransition one = new FSMTransition("1");
    g.addVertex(one);
    FSMTransition two = new FSMTransition("2");
    g.addVertex(two);
    FSMTransition three = new FSMTransition("3");
    g.addVertex(three);
    // Note that the default is for undirected edges, our Edges are Strings.
    g.addEdge("Edge-A", one, two); // Note that Java 1.5 auto-boxes primitives
    g.addEdge("Edge-B", two, three);

// The Layout<V, E> is parameterized by the vertex and edge types
    Layout<FSMTransition, String> layout = new CircleLayout<FSMTransition, String>(g);
    layout.setSize(new Dimension(300, 300)); // sets the initial size of the space
// The BasicVisualizationServer<V,E> is parameterized by the edge types
    BasicVisualizationServer<FSMTransition, String> vv = new BasicVisualizationServer<FSMTransition, String>(layout);
    vv.setPreferredSize(new Dimension(350, 350)); //Sets the viewing area size
    Transformer<FSMTransition, Paint> vertexPaint = new Transformer<FSMTransition, Paint>() {
      public Paint transform(FSMTransition t) {
        return Color.GREEN;
      }
    };
    vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
//    vv.getRenderContext().setVertexShapeTransformer(new EllipseVertexTransformer());
    JFrame frame = new JFrame("Simple Graph View");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(vv);
    frame.pack();
    frame.setVisible(true);
*/

    FSMBuildVisualizer gv = new FSMBuildVisualizer();
    OSMOTester osmo = new OSMOTester(new CalculatorModel());
    osmo.addTestEndCondition(new Length(15));
    osmo.addSuiteEndCondition(new Length(5));
    osmo.addListener(gv);
    osmo.generate();
    System.out.println("s:" + gv.graph.getVertexCount());
  }
}
