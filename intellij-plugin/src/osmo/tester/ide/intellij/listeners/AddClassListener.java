package osmo.tester.ide.intellij.listeners;

import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.ide.intellij.OSMORunConfigEditor;
import osmo.tester.ide.intellij.OSMORunParameters;
import osmo.tester.ide.intellij.filters.ModelObjectFilter;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class AddClassListener implements ActionListener {
  private final Project project;
  private final OSMORunParameters parameters;
  private final DefaultListModel<String> listModel;

  public AddClassListener(Project project, OSMORunConfigEditor editor) {
    this.project = project;
    this.parameters = editor.getRunParameters();
    this.listModel = editor.getClassesListModel();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    TreeClassChooserFactory factory = TreeClassChooserFactory.getInstance(project);
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    TreeClassChooser chooser = factory.createWithInnerClassesScopeChooser("Add test model class", scope, new ModelObjectFilter(project), null);
    //InheritanceClassChooser("Add test model class", scope, ecClass, null);
    chooser.showDialog();
    PsiClass selected = chooser.getSelected();
    //the chooser seems to return null in some cases (if you cancel or don't change the selection maybe?)
    if (selected != null) {
      String qualifiedName = selected.getQualifiedName();
      parameters.addClass(qualifiedName);
      listModel.addElement(qualifiedName);
    }
  }
}
