package osmo.tester.ide.intellij;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

/** @author Teemu Kanstren */
public class Resources {

  private static Icon load(String path) {
    return IconLoader.getIcon(path, Resources.class);
  }

  public static final Icon RUN_OSMO = load("resources/osmo.png"); // 16x16
  public static final Icon PLUS_ICON = load("resources/plus.png");
  public static final Icon MINUS_ICON = load("resources/minus.png");
}
