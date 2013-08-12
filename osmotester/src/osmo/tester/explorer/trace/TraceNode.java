package osmo.tester.explorer.trace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to maintain a trace of test generation.
 *
 * @author Teemu Kanstren
 */
public class TraceNode {
  /** The name of transition/test step taken. */
  private final String name;
  /** True if taken while in exploration mode, used to visualize different nodes in different ways. */
  private final boolean exploring;
  /** The parent node in the trace tree. */
  private final TraceNode parent;
  /** Child nodes in the trace tree. */
  private final List<TraceNode> children = new ArrayList<>();
  /** Used to maintain count of transitions taken to give each new one a unique id as in name_counter */
  private static Map<String, Integer> counts = new LinkedHashMap<>();

  public TraceNode(String name, TraceNode parent, boolean exploring) {
    this.name = name;
    this.parent = parent;
    this.exploring = exploring;
  }

  public TraceNode add(String childName, boolean exploring) {
    Integer count = counts.get(childName);
    if (count == null) {
      count = 0;
    }
    //create a unique name as graphviz will otherwise think it is always the same node and draw a state machine type
    //graph, whereas we want a sequence/tree type graph
    count++;
    counts.put(childName, count);
    childName += "_" + count;

    TraceNode childNode = new TraceNode(childName, this, exploring);
    children.add(childNode);
    return childNode;
  }

  public String getName() {
    return name;
  }

  public TraceNode getParent() {
    return parent;
  }

  public List<TraceNode> getChildren() {
    return children;
  }

  public static void reset() {
    counts.clear();
  }

  public boolean isExploring() {
    return exploring;
  }

  public static Map<String, Integer> getCounts() {
    return counts;
  }
}
