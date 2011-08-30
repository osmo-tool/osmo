package osmo.miner.gui.mainform;

import osmo.miner.gui.mainform.plainmodel.PMForm;
import osmo.miner.gui.mainform.testmodel.TestForm;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;

public class MainForm extends JFrame {
  private MOListModel listModel = new MOListModel();
  private PMForm pmForm = new PMForm();
  private TestForm testForm = new TestForm();

  public MainForm() throws HeadlessException {
    super();
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
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addListSelectionListener(new MOSelectionListener(this));
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

//    JPanel panel_2 = new JPanel();
    // getContentPane().add(panel_2, BorderLayout.CENTER);
//    panel_2.setLayout(new BorderLayout(0, 0));

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Plain", pmForm);
    tabs.addTab("Test", testForm);

    JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, tabs);
    splitter.setOneTouchExpandable(true);
    getContentPane().add(splitter, BorderLayout.CENTER);

    setVisible(true);
  }

  public MOListModel getListModel() {
    return listModel;
  }

  public void updateSelection(ModelObject mo) {
    pmForm.updateWith(mo);
  }
}

