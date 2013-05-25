package osmo.tester.ide.intellij.listeners;

import com.intellij.execution.ui.ClassBrowser;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.ide.intellij.OSMORunParameters;

import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class ECChoiceListener implements ActionListener {
  private final String title;
  private final Project project;
  private final JTextField ecField;
  private final ConfigurationModuleSelector moduleSelector;

  public ECChoiceListener(Project project, String title, JTextField ecField, ConfigurationModuleSelector moduleSelector) {
    this.title = title;
    this.project = project;
    this.ecField = ecField;
    this.moduleSelector = moduleSelector;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    TreeClassChooserFactory factory = TreeClassChooserFactory.getInstance(project);
    final Module module = moduleSelector.getModule();
    GlobalSearchScope scope = null;
    if (module != null) {
      scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
    } else {
      scope = GlobalSearchScope.allScope(project);
    }
    PsiClass ecClass = JavaPsiFacade.getInstance(project).findClass(EndCondition.class.getName(), scope);
    PsiClass current = JavaPsiFacade.getInstance(project).findClass(ecField.getText(), scope);
    
    TreeClassChooser chooser = factory.createInheritanceClassChooser(title, scope, ecClass, current);
    chooser.showDialog();
    PsiClass selected = chooser.getSelected();
    //the chooser seems to return null in some cases (if you cancel or don't change the selection maybe?)
    if (selected != null) {
      ecField.setText(selected.getQualifiedName());
    }
  }
}
