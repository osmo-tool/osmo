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
import osmo.tester.generator.endcondition.EndCondition;

import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class AlgorithmChoiceListener implements ActionListener {
  private final Project project;
  private final JTextField field;

  public AlgorithmChoiceListener(Project project, JTextField field) {
    this.project = project;
    this.field = field;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    TreeClassChooserFactory factory = TreeClassChooserFactory.getInstance(project);
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiClass ecClass = JavaPsiFacade.getInstance(project).findClass(FSMTraversalAlgorithm.class.getName(), scope);
    PsiClass current = JavaPsiFacade.getInstance(project).findClass(field.getText(), scope);
    
    TreeClassChooser chooser = factory.createInheritanceClassChooser("Choose test generation algorithm", scope, ecClass, current);
    chooser.showDialog();
    PsiClass selected = chooser.getSelected();
    //the chooser seems to return null in some cases (if you cancel or don't change the selection maybe?)
    if (selected != null) {
      field.setText(selected.getQualifiedName());
    }
  }
}
