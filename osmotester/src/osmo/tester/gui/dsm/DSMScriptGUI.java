package osmo.tester.gui.dsm;

import osmo.tester.gui.ModelHelper;

import javax.swing.*;
import java.awt.*;

/** @author Teemu Kanstren */
public class DSMScriptGUI extends JFrame {
  public DSMScriptGUI() {
    super("DSM Script Builder");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 600, 460);

    JList transitionList = new JList();
    transitionList.setFixedCellWidth(150);
    transitionList.setModel(new ModelHelper());
    JScrollPane transitionPane = new JScrollPane(transitionList);

    JList variableList = new JList();
    variableList.setModel(new ModelHelper());
    JScrollPane variablePane = new JScrollPane(variableList);

    JPanel settingsPanel = new JPanel();
    settingsPanel.setLayout(new BorderLayout());
    JComboBox algorithmCombo = new JComboBox();
    algorithmCombo.setModel(new DefaultComboBoxModel(new String[]{"RandomAlgorithm", "BalancingAlgorithm", "WeightedAlgorithm"}));
    JButton scriptButton = new JButton("Write Script");
    JPanel p1 = new JPanel();
    p1.setLayout(new GridLayout(4, 1));
    p1.add(Box.createVerticalStrut(8));
    p1.add(algorithmCombo);
    p1.add(Box.createVerticalStrut(8));
    p1.add(scriptButton);
    settingsPanel.add(p1, BorderLayout.NORTH);

    JPanel ruleDefinitionPanel = new JPanel();
    JComboBox ruleCombo = new JComboBox();
    ruleCombo.setModel(new DefaultComboBoxModel(new String[]{">=", "<=", "=="}));
    JTextField ruleField = new JTextField(5);
    JButton addButton = new JButton("Add");
//    ruleDefinitionPanel.add(ruleCombo, BorderLayout.NORTH);
//    ruleDefinitionPanel.add(ruleField, BorderLayout.NORTH);
//    ruleDefinitionPanel.add(addButton, BorderLayout.NORTH);
    ruleDefinitionPanel.add(ruleCombo);
    ruleDefinitionPanel.add(ruleField);
    ruleDefinitionPanel.add(addButton);

    JPanel varDefinitionPanel = new JPanel();
//    varDefinitionPanel.setLayout(new BorderLayout());
    JTextField varField = new JTextField(5);
    JButton addVarButton = new JButton("Add");
    varDefinitionPanel.add(varField);
    varDefinitionPanel.add(addVarButton);
//    varDefinitionPanel.add(varField, BorderLayout.NORTH);
//    varDefinitionPanel.add(addVarButton, BorderLayout.NORTH);

    Box definitions = Box.createVerticalBox();
    definitions.add(Box.createVerticalStrut(20));
    definitions.add(ruleDefinitionPanel);
    definitions.add(Box.createVerticalStrut(20));
    definitions.add(varDefinitionPanel);

    Box fsmBox = Box.createVerticalBox();
//    JPanel left = new JPanel();
    fsmBox.add(new JLabel("Transitions"));
    fsmBox.add(transitionPane);
    fsmBox.add(new JLabel("Variables"));
    fsmBox.add(variablePane);

    Box ruleBox = Box.createVerticalBox();
    ruleBox.add(new JLabel("Transitions"));
    JList transitionRules = new JList();
    transitionRules.setModel(new ModelHelper());
    transitionRules.setFixedCellWidth(150);
    JScrollPane ttPane = new JScrollPane(transitionRules);
    ruleBox.add(ttPane);
    ruleBox.add(new JLabel("Variables"));
    JList variableRules = new JList();
    variableRules.setModel(new ModelHelper());
    JScrollPane vtPane = new JScrollPane(variableRules);
    ruleBox.add(vtPane);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(settingsPanel, BorderLayout.WEST);
    JPanel secondPanel = new JPanel();
    secondPanel.setLayout(new BorderLayout());
    mainPanel.add(secondPanel, BorderLayout.CENTER);
    secondPanel.add(fsmBox, BorderLayout.WEST);
    secondPanel.add(definitions, BorderLayout.CENTER);
    secondPanel.add(ruleBox, BorderLayout.EAST);
    getContentPane().add(mainPanel, BorderLayout.CENTER);

  }

  public static void main(String[] args) {
    new DSMScriptGUI().setVisible(true);
  }
}
