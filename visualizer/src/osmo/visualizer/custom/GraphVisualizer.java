package osmo.visualizer.custom;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class GraphVisualizer extends JFrame implements Visualizer {

  private static final long serialVersionUID = 7870534648148858783L;

  private JTabbedPane tabbedPane = null;
  private mxGraph graph = null;

  private Object currentState = null;
  private int stateInd = 0;

  public GraphVisualizer(String title) {
    super(title);
    tabbedPane = new JTabbedPane();
    getContentPane().add(tabbedPane);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1024, 768);
    setVisible(true);
  }

  @Override
  public void testSuiteStart(String testSuiteID) {
    // TODO Auto-generated method stub

  }

  @Override
  public void testSuiteStop(int testCount) {
    // TODO Auto-generated method stub

  }

  @Override
  public void testCaseStart(String testCaseID) {
    currentState = null;
    stateInd = 0;
    graph = new mxGraph();
    mxGraphComponent graphComponent = new mxGraphComponent(graph);
    tabbedPane.addTab(testCaseID, graphComponent);
  }

  @Override
  public void testCaseStop(String testCaseID) {
    // TODO Auto-generated method stub

  }

  @Override
  public void stateTransition(Object oldState, Object newState, String message) {
    Object parent = graph.getDefaultParent();
    graph.getModel().beginUpdate();
    try {
      if (currentState == null) {
        currentState = graph.insertVertex(parent, null, newState.toString(), 200, 20 + (stateInd * 80), 225, 30);
      } else {
        Object state = graph.insertVertex(parent, null, newState.toString(), 200, 20 + (stateInd * 80), 225, 30);
        graph.insertEdge(parent, null, message, currentState, state);
        currentState = state;
      }
    } finally {
      graph.getModel().endUpdate();
    }
    stateInd++;
  }

  @Override
  public void info(String text, String relatedState) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visualize() {
    // TODO Auto-generated method stub

  }
}
