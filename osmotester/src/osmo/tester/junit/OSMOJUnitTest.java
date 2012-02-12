package osmo.tester.junit;

import org.junit.runners.model.FrameworkMethod;
import osmo.tester.generator.MainGenerator;

import java.lang.reflect.Method;

/**
 * This class is given to JUnit to represent a test case that will be executed.
 * JUnit uses this to give a list of test cases to the execution framework (IDE, Ant, ...).
 * When JUnit actually tries to execute the test, OSMO Tester generates the test and executes
 * it on the fly.
 *
 * @author Teemu Kanstren
 */
public class OSMOJUnitTest extends FrameworkMethod {
  /** The number of this test case, used to generate an ID value. */
  private static int index = 1;
  /** The name of the test case. */
  private final String name;
  /** The generator for generating test cases. */
  private final MainGenerator generator;

  public OSMOJUnitTest(MainGenerator generator, Method method) {
    super(method);
    this.generator = generator;
    this.name = "test" + index;
    index++;
  }

  @Override
  public Object invokeExplosively(Object target, Object... params) throws Throwable {
    //we switch the target to this object and ignore the given target
    //since we just want to generate the stuff with "execute" method in this class
    return super.invokeExplosively(this, params);
  }

  /**
   * We use this to generate the test on the fly. The name is coded to match the value given for
   * reflection in {@link OSMORunner}.
   */
  public void execute() {
//    System.out.println("TEST EXECUTES "+name);
    generator.nextTest();
//    System.out.println();
  }

  /**
   * Provides a name for the test case.
   *
   * @return The value "test"+index.
   */
  @Override
  public String getName() {
    return name;
  }
}
