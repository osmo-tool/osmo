package osmo.tester.generator.multi;

import osmo.tester.OSMOConfiguration;

/** @author Teemu Kanstren */
public class MOSMOConfiguration extends OSMOConfiguration {
  public static final String ERROR_MSG = "MultiOSMO only supports factories, not direct adding of model objects.";
  
  @Override
  public void addModelObject(Object modelObject) {
    throw new UnsupportedOperationException(ERROR_MSG);
  }

  @Override
  public void addModelObject(String prefix, Object modelObject) {
    throw new UnsupportedOperationException(ERROR_MSG);
  }
}
