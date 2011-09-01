package osmo.miner.gui.mainform.programmodel;

import osmo.miner.gui.TreeMouseListener;
import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.gui.mainform.ModelObject;
import osmo.miner.log.Logger;
import osmo.miner.miner.program.MultiProgramModelMiner;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;
import osmo.miner.parser.xml.ProgramHandler;
import osmo.miner.parser.xml.XmlParser;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class AllForm extends JPanel {
  private static final Logger log = new Logger(AllForm.class);
  private JTree tree;
  private Node rootNode = null;
  private final ProgramParser parser;

  public AllForm(ProgramParser parser) {
    this.parser = parser;
    rootNode = new Node(null, "Model", new ArrayList<ValuePair>());
    tree = new JTree(rootNode);
    tree.addMouseListener(new TreeMouseListener(tree));
    DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
    renderer.setOpenIcon(null);
    renderer.setClosedIcon(null);
    JScrollPane scrollPane = new JScrollPane();
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
    scrollPane.setViewportView(tree);
  }

  public void updateWith(List<ModelObject> modelObjects) {
    Collection<Program> programs = new ArrayList<Program>();
    for (ModelObject mo : modelObjects) {
      programs.add(parser.programFor(mo));
    }
    MultiProgramModelMiner miner = new MultiProgramModelMiner();
    Node root = miner.nodeFor(programs);
    root.cloneTo(this.rootNode);
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    model.reload();
  }
}
