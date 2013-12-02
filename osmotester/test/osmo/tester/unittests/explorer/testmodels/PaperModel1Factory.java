package osmo.tester.unittests.explorer.testmodels;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class PaperModel1Factory implements ModelFactory {
  private final PrintStream ps;

  public PaperModel1Factory() {
    this.ps = System.out;
  }

  public PaperModel1Factory(PrintStream ps) {
    this.ps = ps;
  }

  @Override
  public void createModelObjects(TestModels addHere) {
    addHere.add(new PaperModel1(ps));
  }
}
