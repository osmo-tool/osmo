package osmo.tester.ide.intellij.listeners;

import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.ide.intellij.OSMORunConfigEditor;
import osmo.tester.model.ModelFactory;

import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/** @author Teemu Kanstren */
public class PackageFactoryListener implements ActionListener {
  private final JTextField factoryField;
  private final JTextField packageField;
  private final Project project;
  private final JRadioButton factory;

  public PackageFactoryListener(OSMORunConfigEditor editor, Project project, JRadioButton factoryButton) {
    this.factoryField = editor.getFactoryField();
    this.packageField = editor.getPackageField();
    this.project = project;
    this.factory = factoryButton;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (factory.isSelected()) {
      TreeClassChooserFactory factory = TreeClassChooserFactory.getInstance(project);
      GlobalSearchScope scope = GlobalSearchScope.allScope(project);
      PsiClass ecClass = JavaPsiFacade.getInstance(project).findClass(ModelFactory.class.getName(), scope);
      PsiClass current = JavaPsiFacade.getInstance(project).findClass(factoryField.getText(), scope);

      TreeClassChooser chooser = factory.createInheritanceClassChooser("Choose model factory class", scope, ecClass, current);
      chooser.showDialog();
      PsiClass selected = chooser.getSelected();
      //the chooser seems to return null in some cases (if you cancel or don't change the selection maybe?)
      if (selected != null) {
        factoryField.setText(selected.getQualifiedName());
      }
    } else {
      PackageChooserDialog dialog = new PackageChooserDialog("Choose Package", project);
      dialog.show();
      PsiPackage value = dialog.getSelectedPackage();
      packageField.setText(value.getQualifiedName());
    }
  }
}
