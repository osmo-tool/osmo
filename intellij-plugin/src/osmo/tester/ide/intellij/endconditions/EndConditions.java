package osmo.tester.ide.intellij.endconditions;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Endless;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.generator.endcondition.StateCoverage;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.generator.endcondition.structure.ElementCoverage;
import osmo.tester.generator.endcondition.structure.StepCoverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Teemu Kanstren */
public class EndConditions {
  private static Map<String, EndConditionConfiguration> endConditions = new HashMap<>();
  
  static {
    addConfiguration(Length.class, new LengthConfiguration());
    addConfiguration(LengthProbability.class, new LengthProbabilityConfiguration());
    addConfiguration(Probability.class, new ProbabilityConfiguration());
    addConfiguration(Endless.class, new EndlessConfiguration());
    addConfiguration(ElementCoverage.class, new ElementCoverageConfiguration());
    addConfiguration(StepCoverage.class, new StepCoverageConfiguration());
    addConfiguration(StateCoverage.class, new StateCoverageConfiguration());
    addConfiguration(Time.class, new TimeConfiguration());
  }
  
  public static void addConfiguration(Class clazz, EndConditionConfiguration config) {
    endConditions.put(clazz.getName(), config);
  }
  
  public static EndConditionConfiguration getConfigurationFor(String className) {
    return endConditions.get(className);
  }
}
