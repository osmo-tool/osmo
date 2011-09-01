package osmo.miner.gui.mainform;

import osmo.miner.gui.mainform.plainmodel.PMForm;
import osmo.miner.gui.mainform.programmodel.AllForm;
import osmo.miner.gui.mainform.programmodel.ProgramForm;
import osmo.miner.gui.mainform.programmodel.ProgramParser;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainForm extends JFrame {
  private final MOListModel listModel;
  private final PMForm pmForm = new PMForm();
  private final ProgramForm programForm;
  private final AllForm allForm;

  public MainForm() throws HeadlessException {
    super();

    List<ModelObject> objects = new ArrayList<ModelObject>();
    listModel = new MOListModel(objects);

    ProgramParser parser = new ProgramParser();
    programForm = new ProgramForm(parser);
    allForm = new AllForm(parser);

    setTitle("OSMO Miner");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
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

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Plain", pmForm);
    tabs.addTab("Test", programForm);
    tabs.addTab("All", allForm);
    tabs.addChangeListener(new TabChangeListener(objects, allForm));

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
    programForm.updateWith(mo);
  }
}

