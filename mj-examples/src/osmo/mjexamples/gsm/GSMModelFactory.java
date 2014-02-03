package osmo.mjexamples.gsm;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class GSMModelFactory implements ModelFactory {
  private final PrintStream out;

  public GSMModelFactory() {
    this.out = null;
  }

  public GSMModelFactory(PrintStream out) {
    this.out = out;
  }

  @Override
  public void createModelObjects(TestModels addHere) {
    SimCard sim = new SimCard(new SimCardAdaptor());
    sim.out = out;
    addHere.add(sim);
  }
}
