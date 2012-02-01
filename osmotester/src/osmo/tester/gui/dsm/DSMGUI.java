package osmo.tester.gui.dsm;

import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;
import osmo.tester.model.dataflow.SearchableInput;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A user interface for building DSM scripts.
 * The scripts can define how many times a transition should be taken in test generation.
 * They can also define what values variables should have.
 *
 * @author Teemu Kanstren
 */
public class DSMGUI extends JFrame {
  /** Number of times for transition rule. */
  private JTextField trnRuleField;
  /** Variable values for variable. */
  private JTextField varRuleField;
  /** For defining the model factory (that creates the model objects). */
  private JTextField modelFactoryField;
  /** The String list of transition rules. */
  private List<String> transitionRules = new ArrayList<String>();
  /** The String list of variable rules. */
  private List<String> variableRules = new ArrayList<String>();
  /** The String list of variable options. */
  private List<String> variableOptions = new ArrayList<String>();
  private JTextField varOptionField;

  public static void main(String[] args) {
    DSMGUI g = new DSMGUI(new ArrayList<String>(), new ArrayList<String>());
    g.setVisible(true);
  }

  /**
   * Creates the GUI.
   *
   * @param fsm The FSM that defines what transitions and variables are available.
   * @wbp.parser.constructor
   */
  public DSMGUI(FSM fsm) {
    setNimbus();
    Collection<FSMTransition> fsmTransitions = fsm.getTransitions();
    Collection<SearchableInput> inputs = fsm.getSearchableInputs();
    Collection<VariableField> stateVariables = fsm.getStateVariables();

    List<String> transitions = new ArrayList<String>();
    for (FSMTransition transition : fsmTransitions) {
      transitions.add(transition.getName());
    }
    List<String> variables = new ArrayList<String>();
    for (VariableField stateVariable : stateVariables) {
      variables.add(stateVariable.getName());
    }
    for (SearchableInput input : inputs) {
      variables.add(input.getName());
    }
    init(transitions, variables);
  }

  /**
   * Secondary constructor for passing transitions and variables directly.
   *
   * @param transitions The list of transitions for which rules can be defined.
   * @param variables   The list of variables for which rules can be defined.
   */
  public DSMGUI(List<String> transitions, List<String> variables) {
    init(transitions, variables);
  }

  /**
   * Initializes the GUI, creating elements etc.
   *
   * @param transitions The list of transitions for which rules can be defined.
   * @param variables   The list of variables for which rules can be defined.
   */
  public void init(List<String> transitions, List<String> variables) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("DSM Script Builder");
    setBounds(100, 100, 720, 500);
    setResizable(false);

    final JComboBox algorithmCombo = new JComboBox();
    algorithmCombo.setModel(new DefaultComboBoxModel(new String[]{"Random", "Balancing", "Weighted"}));

    JButton btnWriteScript = new JButton("Write Script");
    btnWriteScript.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DSMScriptWriter writer = new DSMScriptWriter();
        String algorithm = algorithmCombo.getSelectedItem().toString();
        String modelFactory = modelFactoryField.getText();
        writer.write(algorithm, modelFactory, transitionRules, variableRules, variableOptions);
      }
    });
    JLabel lblTransitions = new JLabel("Transitions:");
    JScrollPane trnScrollPane = new JScrollPane();
    JLabel lblVariables = new JLabel("Variables:");
    JScrollPane varScrollPane = new JScrollPane();

    final JComboBox ruleCombo = new JComboBox();
    ruleCombo.setModel(new DefaultComboBoxModel(new String[]{">=", "<=", "=="}));

    JButton btnTrnAdd = new JButton("+");

    trnRuleField = new JTextField();
    trnRuleField.setColumns(5);

    JLabel lblTransitionRules = new JLabel("Transition coverage requirements:");

    JLabel lblVariableRules = new JLabel("Variable coverage requirements:");

    JScrollPane trnRulePane = new JScrollPane();

    JScrollPane varRulePane = new JScrollPane();

    varRuleField = new JTextField();
    varRuleField.setColumns(9);

    JButton btnVarAdd = new JButton("+");

    JLabel lblModelFactory = new JLabel("Model Factory:");

    modelFactoryField = new JTextField();
    modelFactoryField.setColumns(12);

    final JList trnRuleList = new JList();

    JButton btnRemoveTrn = new JButton("-");
    btnRemoveTrn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = trnRuleList.getSelectedIndex();
        if (index < 0) {
          JOptionPane.showMessageDialog(DSMGUI.this, "No transition rule selected!");
          return;
        }
        transitionRules.remove(index);
        trnRuleList.setModel(new StringListModel(transitionRules));
      }
    });

    final JList varRuleList = new JList();

    JButton btnRemoveVar = new JButton("-");
    btnRemoveVar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = varRuleList.getSelectedIndex();
        if (index < 0) {
          JOptionPane.showMessageDialog(DSMGUI.this, "No variable rule selected!");
          return;
        }
        variableRules.remove(index);
        varRuleList.setModel(new StringListModel(variableRules));
      }
    });

    JLabel lblAlgorithm = new JLabel("Algorithm:");

    JLabel lblVariableValueOptions = new JLabel("Variable value options:");

    JScrollPane varOptionPane = new JScrollPane();

    varOptionField = new JTextField();
    varOptionField.setColumns(9);

    JButton btnVarOptionAdd = new JButton("+");

    JButton btnVarOptionRemove = new JButton("-");

    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
    	groupLayout.createParallelGroup(Alignment.LEADING)
    		.addGroup(groupLayout.createSequentialGroup()
    			.addContainerGap()
    			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    				.addComponent(algorithmCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    				.addComponent(modelFactoryField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    				.addComponent(btnWriteScript)
    				.addComponent(lblModelFactory)
    				.addComponent(lblAlgorithm))
    			.addPreferredGap(ComponentPlacement.RELATED)
    			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    				.addComponent(lblVariables)
    				.addComponent(lblTransitions)
    				.addComponent(trnScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
    				.addComponent(varScrollPane, 0, 0, Short.MAX_VALUE))
    			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    				.addGroup(groupLayout.createSequentialGroup()
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
    						.addGroup(groupLayout.createSequentialGroup()
    							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
    								.addComponent(btnTrnAdd)
    								.addComponent(ruleCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    								.addComponent(trnRuleField, GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
    								.addComponent(btnRemoveTrn)))
    						.addComponent(varRuleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    						.addGroup(groupLayout.createSequentialGroup()
    							.addComponent(btnVarOptionAdd)
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addComponent(btnVarOptionRemove)
    							.addGap(23))
    						.addComponent(varOptionField))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    						.addComponent(lblVariableValueOptions)
    						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    							.addGroup(groupLayout.createSequentialGroup()
    								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    									.addComponent(varRulePane, 0, 0, Short.MAX_VALUE)
    									.addComponent(lblTransitionRules)
    									.addComponent(trnRulePane, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
    									.addComponent(lblVariableRules))
    								.addPreferredGap(ComponentPlacement.RELATED))
    							.addGroup(groupLayout.createSequentialGroup()
    								.addComponent(varOptionPane, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
    								.addGap(1)))))
    				.addGroup(groupLayout.createSequentialGroup()
    					.addGap(20)
    					.addComponent(btnVarAdd)
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addComponent(btnRemoveVar)
    					.addGap(44)))
    			.addGap(33))
    );
    groupLayout.setVerticalGroup(
    	groupLayout.createParallelGroup(Alignment.LEADING)
    		.addGroup(groupLayout.createSequentialGroup()
    			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    				.addGroup(groupLayout.createSequentialGroup()
    					.addGap(4)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(lblTransitions)
    						.addComponent(lblAlgorithm)
    						.addComponent(lblTransitionRules))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    						.addGroup(groupLayout.createSequentialGroup()
    							.addComponent(algorithmCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addComponent(lblModelFactory)
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addComponent(modelFactoryField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    							.addPreferredGap(ComponentPlacement.UNRELATED)
    							.addComponent(btnWriteScript))
    						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    							.addComponent(trnScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
    							.addComponent(trnRulePane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
    					.addPreferredGap(ComponentPlacement.UNRELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    						.addGroup(groupLayout.createSequentialGroup()
    							.addComponent(lblVariables)
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    								.addComponent(varScrollPane, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
    								.addGroup(groupLayout.createSequentialGroup()
    									.addGap(17)
    									.addComponent(varRuleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    									.addGap(3)
    									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    										.addComponent(btnVarAdd)
    										.addComponent(btnRemoveVar))
    									.addGap(19)
    									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    										.addComponent(lblVariableValueOptions)
    										.addGroup(groupLayout.createSequentialGroup()
    											.addGap(28)
    											.addComponent(varOptionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    											.addGap(1)
    											.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    												.addComponent(btnVarOptionAdd)
    												.addComponent(btnVarOptionRemove))))
    									.addGap(39))))
    						.addGroup(groupLayout.createSequentialGroup()
    							.addComponent(lblVariableRules)
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addComponent(varRulePane, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
    							.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
    							.addComponent(varOptionPane, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
    							.addGap(15))))
    				.addGroup(groupLayout.createSequentialGroup()
    					.addGap(49)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(ruleCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    						.addComponent(trnRuleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(btnRemoveTrn)
    						.addComponent(btnTrnAdd))))
    			.addContainerGap())
    );

    final JList varOptionList = new JList();
    varOptionList.setModel(new StringListModel(variableOptions));
    varOptionPane.setViewportView(varOptionList);

    varRulePane.setViewportView(varRuleList);

    trnRuleList.setModel(new StringListModel(transitionRules));
    trnRulePane.setViewportView(trnRuleList);

    final JList varList = new JList();
    varList.setModel(new StringListModel(variables));
    varScrollPane.setViewportView(varList);

    final JList trnList = new JList();
    trnList.setModel(new StringListModel(transitions));
    trnScrollPane.setViewportView(trnList);
    getContentPane().setLayout(groupLayout);

    btnTrnAdd.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Object selectedValue = trnList.getSelectedValue();
        if (selectedValue == null) {
          return;
        }
        String transition = selectedValue.toString();
        String rule = ruleCombo.getSelectedItem().toString();
        String value = trnRuleField.getText();
        try {
          Integer.parseInt(value);
        } catch (NumberFormatException e1) {
          JOptionPane.showMessageDialog(DSMGUI.this, "Value [" + value + "] is not a valid number");
          return;
        }
        transitionRules.add(transition + "," + rule + value);
        trnRuleList.setModel(new StringListModel(transitionRules));
      }
    });
    btnVarAdd.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Object selectedValue = varList.getSelectedValue();
        if (selectedValue == null) {
          return;
        }
        String variable = selectedValue.toString();
        String value = varRuleField.getText();
        variableRules.add(variable + "," + value);
        varRuleList.setModel(new StringListModel(variableRules));
      }
    });
    btnVarOptionAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Object selectedValue = varList.getSelectedValue();
        if (selectedValue == null) {
          return;
        }
        String variable = selectedValue.toString();
        String value = varOptionField.getText();
        variableOptions.add(variable + "," + value);
        varOptionList.setModel(new StringListModel(variableOptions));
      }
    });
    btnVarOptionRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = varOptionList.getSelectedIndex();
        if (index < 0) {
          JOptionPane.showMessageDialog(DSMGUI.this, "No variable option selected!");
          return;
        }
        variableOptions.remove(index);
        varOptionList.setModel(new StringListModel(variableOptions));
      }
    });
  }

  /** Enables the Nimbus look and feel. */
  public void setNimbus() {
    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception e) {
      // If Nimbus is not available, you can set the GUI to another look and feel.
    }
  }
}
