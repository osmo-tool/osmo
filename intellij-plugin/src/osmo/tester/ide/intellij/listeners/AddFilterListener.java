package osmo.tester.ide.intellij.listeners;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.ide.intellij.OSMORunConfigEditor;
import osmo.tester.ide.intellij.OSMORunParameters;

import javax.swing.DefaultListModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class AddFilterListener implements ActionListener {
  private final Project project;
  private final OSMORunParameters parameters;
  private final DefaultListModel<String> listModel;

  public AddFilterListener(Project project, OSMORunConfigEditor editor) {
    this.project = project;
    this.parameters = editor.getRunParameters();
    this.listModel = editor.getFiltersListModel();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    TreeClassChooserFactory factory = TreeClassChooserFactory.getInstance(project);
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiClass filterClass = JavaPsiFacade.getInstance(project).findClass(StepFilter.class.getName(), scope);

    TreeClassChooser chooser = factory.createInheritanceClassChooser("Choose Filter", scope, filterClass, null);
    chooser.showDialog();
    PsiClass selected = chooser.getSelected();
    //the chooser seems to return null in some cases (if you cancel or don't change the selection maybe?)
    if (selected != null) {
      String qualifiedName = selected.getQualifiedName();
      parameters.addFilter(qualifiedName);
      listModel.addElement(qualifiedName);
    }
  }
}
