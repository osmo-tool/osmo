package osmo.tester.junit;

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

/** @author Teemu Kanstren */
public class OSMORunner extends BlockJUnit4ClassRunner {
  protected List<FrameworkMethod> tests = new ArrayList<FrameworkMethod>();

  public OSMORunner(Class<?> klass) throws InitializationError {
    super(klass);
    try {
      computeTests();
    } catch (Exception e) {
      throw new InitializationError(e);
    }
  }

  protected void computeTests() throws Exception {
    tests.addAll(super.computeTestMethods());
    tests.addAll(computeOSMOTests());

    //JUnit expects this..
    createTest();
  }

  protected Collection<? extends FrameworkMethod> computeOSMOTests() throws Exception {
    List<FrameworkMethod> factories = getTestClass().getAnnotatedMethods(OSMOConfigurationFactory.class);
    int factoryCount = factories.size();
    if (factoryCount == 0) {
      throw new IllegalArgumentException("OSMORunner requires a @OsmoConfigurationFactory method");
    }
    if (factoryCount > 1) {
      throw new IllegalArgumentException("OSMORunner supports only one @OsmoConfigurationFactory method (had "+ factoryCount + ")");
    }
    FrameworkMethod method = factories.get(0);

    // Make sure the TestFactory method is static
    if (!Modifier.isStatic(method.getMethod().getModifiers()))
      throw new InitializationError("TestFactory " + method + " must be static.");

    // Execute the method (statically)
    Object obj = method.getMethod().invoke(getTestClass().getJavaClass());
    if (obj == null) {
      throw new IllegalArgumentException("@"+OSMOConfigurationFactory.class.getSimpleName()+" method must return a value of type "+OSMOConfiguration.class.getSimpleName()+" was null");
    }
    if (!(obj instanceof OSMOConfiguration)) {
      throw new IllegalArgumentException("@"+OSMOConfigurationFactory.class.getSimpleName()+" method must return a value of type "+OSMOConfiguration.class.getSimpleName()+" was "+obj.getClass().getSimpleName());
    }
    OSMOConfiguration config = (OSMOConfiguration) obj;

    OSMOTester osmo = new OSMOTester();
    osmo.setConfig(config);
    MainGenerator generator = osmo.initGenerator();
    generator.initSuite();
    config.addListener(new JUnitGenerationListener(config.getJUnitLength(), generator));

    int count = config.getJUnitLength();
    List<FrameworkMethod> tests = new ArrayList<FrameworkMethod>();
    for (int i = 0 ; i < count ; i++) {
      Method execute = OSMOJUnitTest.class.getMethod("execute");
      OSMOJUnitTest test = new OSMOJUnitTest(generator, execute);
      tests.add(test);
    }
    return tests;
  }

  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    return tests;
  }

  @Override
  protected void validateInstanceMethods(List<Throwable> errors) {
    //this should be unused (so the doc says and it is deprecated) but yet without this we nullpointer..
    return;
  }
}
