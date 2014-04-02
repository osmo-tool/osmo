package osmo.tester.ide.intellij.listeners;

import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import osmo.tester.ide.intellij.OSMORunConfigEditor;
import osmo.tester.ide.intellij.OSMORunParameters;
import osmo.tester.ide.intellij.filters.ModelObjectFilter;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class RemoveClassListener implements ActionListener {
  private final OSMORunParameters parameters;
  private final DefaultListModel<String> listModel;
  private final JList list;

  public RemoveClassListener(JList list, OSMORunConfigEditor editor) {
    this.list = list;
    this.parameters = editor.getRunParameters();
    this.listModel = editor.getClassesListModel();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object value = list.getSelectedValue();
    if (value == null) return;
    String selection = value.toString();
    parameters.removeClass(selection);
    listModel.removeElement(selection);
  }
}
