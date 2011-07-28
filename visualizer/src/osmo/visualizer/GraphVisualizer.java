package osmo.visualizer;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import osmo.tester.OSMOTester;
import osmo.tester.examples.CalculatorModel;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.endcondition.Length;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * A simple visualizer for generated test cases based on the JGraphX library.
 *
 * @author Juho Perälä
 */

public class GraphVisualizer extends JFrame implements GenerationListener {
  private static final long serialVersionUID = 7870534648148858783L;
  private JTabbedPane tabbedPane = null;
  private mxGraph graph = null;
  private Object currentState = null;
  private int stateInd = 0;
  private int testCaseID = 0;

  public GraphVisualizer(String title) {
    super(title);
    tabbedPane = new JTabbedPane();
    getContentPane().add(tabbedPane);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1024, 768);
    setVisible(true);
  }

  @Override
  public void guard(String name) {
  }

  @Override
  public void transition(String name) {
    Object parent = graph.getDefaultParent();
    graph.getModel().beginUpdate();
    try {
      if (currentState == null) {
        currentState = graph.insertVertex(parent, null, name, 200, 20 + (stateInd * 80), 140, 30);
      } else {
        Object state = graph.insertVertex(parent, null, name, 200, 20 + (stateInd * 80), 140, 30);
        //you can also set text descriptions on the edges, check the JGraphX docs for details
        graph.insertEdge(parent, null, null, currentState, state);
//        graph.insertEdge(parent, null, messsage, currentState, state);
        currentState = state;
      }
    } finally {
      graph.getModel().endUpdate();
    }
    stateInd++;
  }

  @Override
  public void pre(String name) {
  }

  @Override
  public void post(String name) {
  }

  @Override
  public void testStarted() {
    currentState = null;
    stateInd = 0;
    graph = new mxGraph();
    mxGraphComponent graphComponent = new mxGraphComponent(graph);
    testCaseID++;
    tabbedPane.addTab("TC"+testCaseID, graphComponent);
  }

  @Override
  public void testEnded() {
  }

  @Override
  public void suiteStarted() {
  }

  @Override
  public void suiteEnded() {
  }

  public static void main(String[] args) {
    GraphVisualizer gv = new GraphVisualizer("Test");
    OSMOTester osmo = new OSMOTester(new CalculatorModel());
    osmo.addTestEndCondition(new Length(15));
    osmo.addSuiteEndCondition(new Length(5));
    osmo.addListener(gv);
    osmo.generate();
  }
}
