package osmo.miner.gui.mainform;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JTree;
import javax.swing.JSplitPane;

public class MainForm extends JFrame {

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

    JList list = new JList();
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

    JTree tree = new JTree();
    scrollPane_1.setViewportView(tree);

    JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, panel_2);
    splitter.setOneTouchExpandable(true);
    getContentPane().add(splitter, BorderLayout.CENTER);

    setVisible(true);
  }
}
