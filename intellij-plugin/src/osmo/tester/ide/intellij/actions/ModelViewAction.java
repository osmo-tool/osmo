package osmo.tester.ide.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/** @author Teemu Kanstren */
public class ModelViewAction extends AnAction {
  public void actionPerformed(AnActionEvent e) {
    System.out.println("hello plugin");
  }
}
