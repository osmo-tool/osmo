package osmo.tester.generator.multi;

import osmo.tester.OSMOConfiguration;

/** @author Teemu Kanstren */
public class MOSMOConfiguration extends OSMOConfiguration {
  @Override
  public void addModelObject(Object modelObject) {
    throw new UnsupportedOperationException("MultiOSMO only supports factories.");
  }

  @Override
  public void addModelObject(String prefix, Object modelObject) {
    throw new UnsupportedOperationException("MultiOSMO only supports factories.");
  }
}
