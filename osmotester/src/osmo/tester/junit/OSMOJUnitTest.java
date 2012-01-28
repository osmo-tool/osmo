package osmo.tester.junit;

import org.junit.runners.model.FrameworkMethod;
import osmo.tester.generator.MainGenerator;

import java.lang.reflect.Method;

/** @author Teemu Kanstren */
public class OSMOJUnitTest extends FrameworkMethod {
  private static int index = 1;
  private final String name;
  private final MainGenerator generator;

  public OSMOJUnitTest(MainGenerator generator, Method method) {
    super(method);
    this.generator = generator;
    this.name = "test"+index;
    index++;
  }

  @Override
  public Object invokeExplosively(Object target, Object... params) throws Throwable {
    return super.invokeExplosively(this, params);
  }

  public void execute() {
//    System.out.println("TEST EXECUTES "+name);
    generator.nextTest();
//    System.out.println();
  }

  @Override
  public String getName() {
    return name;
  }
}
