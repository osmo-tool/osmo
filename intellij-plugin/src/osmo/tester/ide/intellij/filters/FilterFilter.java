package osmo.tester.ide.intellij.filters;

import com.intellij.compiler.CompilerConfiguration;
import com.intellij.execution.configurations.ConfigurationUtil;
import com.intellij.ide.util.ClassFilter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.TransitionFilter;

/** 
 * This is a filter to show only EndCondition instances in an IntelliJ dialog.
 * 
 * @author Teemu Kanstren */
public class FilterFilter implements ClassFilter.ClassFilterWithScope {
  private final Project project;
  private final Module module;
  private final PsiClass ecClass;
  private final GlobalSearchScope scope;

  public FilterFilter(Project project, Module module) {
    if (module != null) {
      scope = GlobalSearchScope.moduleScope(module);
    } else {
      scope = GlobalSearchScope.allScope(project);
    }
    this.ecClass = JavaPsiFacade.getInstance(project).findClass(TransitionFilter.class.getName(), scope);
    this.project = project;
    this.module = module;
  }

  @Override
  public GlobalSearchScope getScope() {
    return scope;
  }

  @Override
  public boolean isAccepted(final PsiClass aClass) {
    return ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
      @Override
      public Boolean compute() {
        return ConfigurationUtil.PUBLIC_INSTANTIATABLE_CLASS.value(aClass) &&
                (aClass.isInheritor(ecClass, true)
                && !CompilerConfiguration.getInstance(project).isExcludedFromCompilation(PsiUtilCore.getVirtualFile(aClass)));
      }
    });
  }
}
