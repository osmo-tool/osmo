package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.CoverageMethod;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.CoverageValue} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class CoverageValueParser implements AnnotationParser {
  private static final Logger log = new Logger(CoverageValueParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    CoverageValue cv = (CoverageValue) parameters.getAnnotation();

    Method method = parameters.getMethod();
    Class<?> returnType = method.getReturnType();
    String name = "@"+CoverageValue.class.getSimpleName();
    if (returnType != String.class) {
      errors.append("Invalid return type for ").append(name).append(" in (\"").append(method.getName()).append("()\")" +
              ":").append(returnType).append(". Should be String.\n");
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1) {
      errors.append(name).append(" methods must have 1 parameter (").append(TestCaseStep.class).append("): \"")
              .append(method.getName()).append("()\" has ").append(parameterTypes.length).append(" parameters.\n");
    }
      
    if (parameterTypes.length > 0 && parameterTypes[0] != TestCaseStep.class) {
      errors.append(name).append(" parameter must be of type ").append(TestCaseStep.class).append(": \"").append
              (method.getName()).append("()\" has type ").append(parameterTypes[0]).append("\n");
    }
    
    String variableName = cv.value();
    if (variableName.equals("")) {
      variableName = method.getName();
    }

    String prefix = parameters.getPrefix();
    if (prefix != null && prefix.length() > 0) {
      variableName = prefix+"-"+variableName;
    }

    CoverageMethod cm = new CoverageMethod(variableName, new InvocationTarget(parameters, CoverageValue.class));
    result.getFsm().addCoverageMethod(cm);
  }
}
