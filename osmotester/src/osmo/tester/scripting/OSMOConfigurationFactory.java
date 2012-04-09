package osmo.tester.scripting;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.EndCondition;

import java.util.Collection;

/**
 * A factory providing elements for the DSM generator.
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
