package osmo.tester.ide.intellij;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/** @author Teemu Kanstren */
public class OSMOPluginRegistration implements ApplicationComponent {
  @Override
  public void initComponent() {
  }

  @Override
  public void disposeComponent() {
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "OSMO Tester Plugin";
  }
}
