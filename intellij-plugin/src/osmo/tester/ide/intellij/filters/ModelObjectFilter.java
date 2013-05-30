package osmo.tester.ide.intellij.filters;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.compiler.CompilerConfiguration;
import com.intellij.execution.configurations.ConfigurationUtil;
import com.intellij.ide.util.ClassFilter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.StateName;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.endcondition.EndCondition;

import java.util.Collection;
import java.util.HashSet;

/** 
 * This is a filter to show only classes with OSMO model annotations in an IntelliJ dialog.
 * 
 * @author Teemu Kanstren */
public class ModelObjectFilter implements ClassFilter.ClassFilterWithScope {
  private final Project project;
  private final GlobalSearchScope scope;
  private static Collection<String> annotations = new HashSet<>();
  
  static {
    annotations.add(Transition.class.getName());
    annotations.add(TestStep.class.getName());
    annotations.add(Guard.class.getName());
    annotations.add(Pre.class.getName());
    annotations.add(Post.class.getName());
    annotations.add(BeforeSuite.class.getName());
    annotations.add(BeforeTest.class.getName());
    annotations.add(AfterSuite.class.getName());
    annotations.add(AfterTest.class.getName());
    annotations.add(StateName.class.getName());
    
//    annotations.add(Variable.class.getName());
  }

  public ModelObjectFilter(Project project) {
    scope = GlobalSearchScope.allScope(project);
    this.project = project;
  }

  @Override
  public GlobalSearchScope getScope() {
    return scope;
  }

  @Override
  public boolean isAccepted(final PsiClass aClass) {
    Boolean result = ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
      @Override
      public Boolean compute() {
        boolean instantiable = ConfigurationUtil.PUBLIC_INSTANTIATABLE_CLASS.value(aClass);
        if (!instantiable) return false;
        boolean excluded = CompilerConfiguration.getInstance(project).isExcludedFromCompilation(PsiUtilCore.getVirtualFile(aClass));
        if (excluded) return false;
        PsiMethod[] methods = aClass.getAllMethods();
        for (PsiMethod method : methods) {
          if (AnnotationUtil.isAnnotated(method, annotations)) {
            return true;
          }
        }
        return false;
      }
    });
    return result;
  }
}
