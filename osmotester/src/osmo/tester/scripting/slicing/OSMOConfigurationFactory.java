package osmo.tester.scripting.slicing;

import osmo.tester.OSMOConfiguration;

/**
 * A factory providing elements for the slicer.
 *
 * @author Teemu Kanstren
 */
public interface OSMOConfigurationFactory {
  /**
   * Configuration for test generation.
   *
   * @return User defined configuration for test generation.
   */
  public OSMOConfiguration createConfiguration();
}
