package osmo.tester.reporting.junit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.MainGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The JUnit runner that takes care of identifying the test cases for JUnit and generating them when requested.
 *
 * @author Teemu Kanstren
 */
public class OSMORunner extends BlockJUnit4ClassRunner {
  /** The list of tests to be executed (and generated in our case). */
  protected List<FrameworkMethod> tests = new ArrayList<>();

  public OSMORunner(Class<?> klass) throws InitializationError {
    super(klass);
    try {
      //here we identify the tests we will have
      computeTests();
    } catch (Exception e) {
      throw new InitializationError(e);
    }
  }

  /**
   * Used to identify the tests to be executed and generated.
   * Includes both all @Test tagged methods in the class as well as all tests to be generated by the
   * given OSMO Tester configuration as identified by {@link OSMOConfigurationFactory}.
   *
   * @throws Exception if something goes all wrong (ooh)..
   */
  protected void computeTests() throws Exception {
    tests.addAll(super.computeTestMethods());
    tests.addAll(computeOSMOTests());

    //JUnit expects this..
    createTest();
  }

  /**
   * Creates the list of test that OSMO Tester will generate, and placeholders that delegate test execution
   * to OSMO Tester for generation.
   *
   * @return The set of tests.
   * @throws Exception If there is an error (ooh)..
   */
  protected Collection<? extends FrameworkMethod> computeOSMOTests() throws Exception {
    List<FrameworkMethod> factories = getTestClass().getAnnotatedMethods(OSMOConfigurationFactory.class);
    //first we check that there is exactly one OSMO Configuration provided
    int factoryCount = factories.size();
    if (factoryCount == 0) {
      throw new IllegalArgumentException("OSMORunner requires a @OsmoConfigurationFactory method");
    }
    if (factoryCount > 1) {
      throw new IllegalArgumentException("OSMORunner supports only one @OsmoConfigurationFactory method (had " + factoryCount + ")");
    }
    FrameworkMethod method = factories.get(0);

    // Make sure the TestFactory method is static
    if (!Modifier.isStatic(method.getMethod().getModifiers()))
      throw new InitializationError("TestFactory " + method + " must be static.");

    // Execute the method (statically)
    Object obj = method.getMethod().invoke(getTestClass().getJavaClass());
    if (obj == null) {
      throw new IllegalArgumentException("@" + OSMOConfigurationFactory.class.getSimpleName() + " method must return a value of type " + OSMOConfiguration.class.getSimpleName() + " was null");
    }
    if (!(obj instanceof OSMOConfiguration)) {
      throw new IllegalArgumentException("@" + OSMOConfigurationFactory.class.getSimpleName() + " method must return a value of type " + OSMOConfiguration.class.getSimpleName() + " was " + obj.getClass().getSimpleName());
    }
    OSMOConfiguration config = (OSMOConfiguration) obj;

    int count = config.getJUnitLength();
    if (count <= 0) {
      throw new IllegalArgumentException(OSMOConfiguration.class.getSimpleName() + " with JUnit must define value for JUnitLength");
    }

    //now we configure the test generator according to the given configuration
    OSMOTester osmo = new OSMOTester();
    osmo.setConfig(config);
    MainGenerator generator = osmo.initGenerator();
    //handle initial setups
    generator.initSuite();
    //add the listener to take care of teardown after all tests have been generated
    config.addListener(new JUnitGenerationListener(config.getJUnitLength(), generator));

    List<FrameworkMethod> tests = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Method execute = OSMOJUnitTest.class.getMethod("execute");
      //create the test delegate which points to the "execute" method in OSMOJUnitTest
      OSMOJUnitTest test = new OSMOJUnitTest(generator, execute);
      tests.add(test);
    }
    return tests;
  }

  /**
   * Capture any @Test annotated basic JUnit tests as well if any are defined.
   *
   * @return
   */
  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    return tests;
  }

  /** this should be unused (so the doc says and it is deprecated) but yet without this JUnit nullpointers.. */
  @Override
  protected void validateInstanceMethods(List<Throwable> errors) {
    return;
  }
}
