package osmo.miner.gui.mainform;

import osmo.miner.model.Node;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.*;
import java.awt.BorderLayout;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class MainForm extends JFrame {
  private Node rootNode = new Node(null, "root");
  private DefaultListModel listModel = new DefaultListModel();
  private JTree tree;

  public MainForm() throws HeadlessException {
    super();
    // TODO Auto-generated constructor stub
    setTitle("OSMO Miner");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // getContentPane().setLayout(new FlowLayout());
    getContentPane().setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    // getContentPane().add(panel, BorderLayout.WEST);
    panel.setLayout(new BorderLayout(0, 0));

    JScrollPane scrollPane = new JScrollPane();
    panel.add(scrollPane);

    JList list = new JList(listModel);
    scrollPane.setViewportView(list);

    JLabel lblFiles = new JLabel("Files/Tests");
    panel.add(lblFiles, BorderLayout.NORTH);

    JPanel panel_1 = new JPanel();
    panel.add(panel_1, BorderLayout.SOUTH);
    panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

    JButton btnAdd = new JButton("+");
    btnAdd.addActionListener(new AddButtonListener(this));
    panel_1.add(btnAdd);

    JButton btnRemove = new JButton("-");
    btnRemove.addActionListener(new RemoveButtonListener(this));
    panel_1.add(btnRemove);

    JPanel panel_2 = new JPanel();
    // getContentPane().add(panel_2, BorderLayout.CENTER);
    panel_2.setLayout(new BorderLayout(0, 0));

    JLabel lblModel = new JLabel("Model");
    panel_2.add(lblModel, BorderLayout.NORTH);

    JScrollPane scrollPane_1 = new JScrollPane();
    panel_2.add(scrollPane_1, BorderLayout.CENTER);

    createTree();
    scrollPane_1.setViewportView(tree);

    JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, panel_2);
    splitter.setOneTouchExpandable(true);
    getContentPane().add(splitter, BorderLayout.CENTER);

    setVisible(true);
  }

  private void createTree() {
    tree = new JTree(rootNode);
//    BasicTreeUI ui = (BasicTreeUI) tree.getUI();
//    tree.setShowsRootHandles(false);
    DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
//    URL openImg = getClass().getResource("/osmo/miner/images/down-arrow.png");
//    ImageIcon openIcon = new ImageIcon(openImg);
//    ui.setExpandedIcon(openIcon);
    renderer.setOpenIcon(null);
//    URL closeImg = getClass().getResource("/osmo/miner/images/right-arrow.png");
//    ImageIcon closeIcon = new ImageIcon(closeImg);
//    ui.setCollapsedIcon(closeIcon);
//    ui.setCollapsedIcon(null);
    renderer.setClosedIcon(null);
//    renderer.setLeafIcon(customLeafIcons);
//    tree.setCellRenderer(renderer);
  }

  public DefaultListModel getListModel() {
    return listModel;
  }

  public Node getRootNode() {
    return rootNode;
  }

  public void updateTree() {
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    model.reload();
  }
}
