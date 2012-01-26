package osmo.tester.junit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import osmo.tester.OSMOConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class OSMORunner extends BlockJUnit4ClassRunner {
  protected List<FrameworkMethod> tests = new ArrayList<FrameworkMethod>();

  public OSMORunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  protected void computeTests() throws Exception {
    tests.addAll(super.computeTestMethods());
    tests.addAll(computeOSMOTests());

    //JUnit expects this..
    createTest();
  }

  protected Collection<? extends FrameworkMethod> computeOSMOTests() throws Exception {
    List<FrameworkMethod> tests = new ArrayList<FrameworkMethod>();
    List<FrameworkMethod> factories = getTestClass().getAnnotatedMethods(OSMOConfigurationFactory.class);
    int factoryCount = factories.size();
    if (factoryCount == 0) {
      throw new IllegalArgumentException("OSMORunner requires a @OsmoConfigurationFactory method");
    }
    if (factoryCount > 1) {
      throw new IllegalArgumentException("OSMORunner supports only one @OsmoConfigurationFactory method (had "+ factoryCount + ")");
    }
    FrameworkMethod method = factories.get(0);
//    OSMOConfiguration config = method.invokeExplosively();
    return tests;
  }

  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    return tests;
  }
}
