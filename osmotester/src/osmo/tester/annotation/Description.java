package osmo.tester.annotation;

/**
 * Allows writing descriptions for rules and actions. Also known as guards and test steps.
 * 
 * @author Teemu Kanstren
 */
public @interface Description {
  String[] value();
}
