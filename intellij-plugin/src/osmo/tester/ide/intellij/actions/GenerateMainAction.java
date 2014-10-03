package osmo.tester.ide.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import osmo.tester.ide.intellij.OSMORunConfigEditor;
import osmo.tester.ide.intellij.OSMORunParameters;

/** @author Teemu Kanstren */
public class GenerateMainAction extends AnAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    OSMORunConfigEditor editor = new OSMORunConfigEditor(e.getProject());
    boolean ok = editor.showAndGet();
    if (ok) {
      OSMORunParameters parameters = editor.getRunParameters();
      editor.fill(parameters);
      String src = "public static void main(String[] args) {\n";
      src += "  OSMOConfiguration.setSeed("+parameters.getSeed()+")\n";
      src += "  OSMOTester tester = new OSMOTester();\n";
      src += "  tester.setAlgorithm("+parameters.getAlgorithm()+");\n";
      src += "  EndCondition tec = ";
      if (parameters.getTestEndCondition() != null) {
        src += parameters.getTestEndCondition().getCreateString();
      } else {
        src += "Define one please;\n";
      }
      src += "  EndCondition sec = ";
      if (parameters.getSuiteEndCondition() != null) {
        src += parameters.getSuiteEndCondition().getCreateString();
      } else {
        src += "Define one please;\n";
      }
      src += "  tester.setTestEndCondition(tec);\n";
      src += "  tester.setSuiteEndCondition(sec);\n";
      src += "  tester.generate();\n";
      src += "}\n";
      Messages.showDialog(e.getProject(), src, "Here is your stuff", new String[0], 0, null);
    }
  }
}
