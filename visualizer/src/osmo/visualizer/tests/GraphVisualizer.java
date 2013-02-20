package osmo.visualizer.tests;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

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
  public void init(FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestStep step) {
    Object parent = graph.getDefaultParent();
    graph.getModel().beginUpdate();
    try {
      if (currentState == null) {
        currentState = graph.insertVertex(parent, null, step.getName(), 200, 20 + (stateInd * 80), 140, 30);
      } else {
        Object state = graph.insertVertex(parent, null, step.getName(), 200, 20 + (stateInd * 80), 140, 30);
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
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  @Override
  public void testStarted(TestCase test) {
    currentState = null;
    stateInd = 0;
    graph = new mxGraph();
    mxGraphComponent graphComponent = new mxGraphComponent(graph);
    testCaseID++;
    tabbedPane.addTab("TC"+testCaseID, graphComponent);
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

  @Override
  public void testError(TestCase test, Exception error) {
  }
}
