package osmo.tester.explorer.trace;

import osmo.common.log.Logger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Writes a test exploration trace in GraphViz DOT format.
 * The ExplorationAlgorithm of OSMO Explorer writes the trace for every step explored.
 * This class only does something if the 'enabled' flag is, well, enabled.
 * To use, you need to create a new instance for each test case being generated/traced.
 * After this, you create a set of {@link TraceNode} elements.
 * Finally, you call the write() method to spill out the dot formatted file.
 * The write() call will also invoke a command line 'script' to create a .png file from the trace using graphviz.
 * So naturally, you need to have installed graphviz and have it on the path.
 * Everything is stored in directory "osmo-dot" under the working directory.
 * Upon start, you should call deleteFiles() to clear the directory.
 * {@link osmo.tester.explorer.ExplorerAlgorithm} does this for you.
 *
 * @author Teemu Kanstren
 */
public class DOTWriter {
  private static final Logger log = new Logger(DOTWriter.class);
  /** Index of file for this test, meaning the step being explored. */
  private int fileIndex = 0;
  /** Line separator for writing the output. */
  private String ln = System.lineSeparator();
  /** Number of the test case being generated/explored. */
  private final int testIndex;
  /** If false, nothing happens. */
  public static boolean enabled = false;
  /** The directory where the trace is written to. */
  private static final String DIR = "osmo-dot";

  public DOTWriter(int testIndex) {
    this.testIndex = testIndex;
  }

  public int getTestIndex() {
    return testIndex;
  }

  /**
   * Write a given trace into a .dot file and use graphviz to transform that into a graphical representation.
   * The depth and top are used only to create a box in the corner describing these properties of the trace.
   *
   * @param traceRoot Used to write the tree.
   * @param depth     Exploration depth in the current node.
   * @param top       The name of the last element that is a taken step, from this point forward it is 'exploration'.
   */
  public void write(TraceNode traceRoot, int depth, String top) {
    if (!enabled) {
      return;
    }
    fileIndex++;
    String path = "path" + testIndex + "_" + fileIndex;
    String output = "digraph " + path + " {" + ln;
    //create the box with depth and top in the corner to describe the trace properties
    output += createPropertyBox(depth, top);
    //create the trace itself in dot format
    output += textFor(traceRoot);
    output += "}";
    writeToFile(DIR, path, output);
    try {
      //run the graphviz dot command to create a PNG file
      String command = "dot -Tpng " + DIR + "/" + path + ".dot -o " + DIR + "/" + path + ".png";
      Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      String errorMsg = "Failed to run DOT. Have you installed Graphviz and put it on path?";
      log.error(errorMsg, e);
      throw new RuntimeException(errorMsg, e);
    }
  }

  /**
   * Create a box to be displayed on the graphical trace, showing properties of the exploration used to generate it.
   *
   * @param depth   The depth of exploration.
   * @param current The current step from which the exploration commences.
   * @return The box description in DOT format.
   */
  private String createPropertyBox(int depth, String current) {
    String label = "depth=" + depth + "\\n";
    label += "current = " + current;
    String box = "properties [shape=box, label=\"" + label + "\"]";
    return box;
  }

  /**
   * Creates the DOT trace for the explored test sequences (a->b and so on).
   * The taken test steps so far are illustrated in black, the explored part in red.
   *
   * @param node The root node from which to generate.
   * @return The trace in DOT format.
   */
  private String textFor(TraceNode node) {
    List<TraceNode> children = node.getChildren();
    String output = "";
    boolean exploring = false;
    for (TraceNode child : children) {
      if (exploring != child.isExploring()) {
        exploring = child.isExploring();
        if (exploring) {
          output += "edge [color=red]" + ln;
        } else {
          output += "edge [color=black]" + ln;
        }
      }
      output += node.getName() + " -> " + child.getName() + ln;
      output += textFor(child);
    }
    return output;
  }

  /**
   * Write the given content to the given file in the trace directory.
   *
   * @param fileName The name of file to write (trace path index).
   * @param content  What to write to file.
   */
  public void writeToFile(String dirPath, String fileName, String content) {
    try {
      Path dir = Paths.get(dirPath);
      Files.createDirectories(dir);
      Path file = Paths.get(dirPath + "/" + fileName + ".dot");
      Files.write(file, content.getBytes("UTF8"), StandardOpenOption.CREATE_NEW);
    } catch (IOException e) {
      log.error("Unable to open graph file for writing", e);
      throw new RuntimeException(e);
    }
  }

  public static void deleteFiles() {
    deleteFiles(DIR);
  }

  /** Delete the trace directory. */
  public static void deleteFiles(String dirPath) {
    try {
      Path dir = Paths.get(dirPath);
      //delete the directory and all files inside it, recursively
      Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
//      log.error("Unable to delete files", e);
//      throw new RuntimeException(e);
    }
  }
}
