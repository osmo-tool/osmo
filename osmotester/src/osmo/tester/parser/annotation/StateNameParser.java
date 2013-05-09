package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.StateName;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.StateName} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class StateNameParser implements AnnotationParser {
  private static Logger log = new Logger(StateNameParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    StateName state = (StateName) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?> returnType = method.getReturnType();
    String name = StateName.class.getSimpleName();
    if (returnType != String.class) {
      errors += "Invalid return type for @"+name+" in (\"" + method.getName() + "()\"):" + returnType + ". Should be String.\n";
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1) {
      errors += name +" methods must have 1 parameter (TestStep): \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }
      
    if (parameterTypes.length > 0 && parameterTypes[0] != TestCaseStep.class) {
      errors += name +" parameter must be of type "+TestCaseStep.class+": \"" + method.getName() + "()\" has type " + parameterTypes[0]+"\n";
    }

    String modelObjectName = FSMTransition.createModelObjectName(parameters.getPrefix(), parameters.getModelClass());
    result.getFsm().addStateNameFor(modelObjectName, new InvocationTarget(parameters, StateName.class));
    return errors;
  }
}
