package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.Group;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.Transition} and {@link osmo.tester.annotation.TestStep} annotations 
 * from the given model object.
 *
 * @author Teemu Kanstren
 */
public class TransitionParser implements AnnotationParser {
  private static Logger log = new Logger(TransitionParser.class);
  private String errors = "";

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    errors = "";
    String type = "";
    Object annotation = parameters.getAnnotation();
    String name = null;
    int weight = 0;
    String group = parameters.getClassAnnotation(Group.class);
    boolean strict = true;
    if (annotation instanceof Transition) {
      Transition t = (Transition) annotation;
      name = t.name();
      //first we try the "name" property which dominates, then the default "value" property
      //since they both have default values of "" this is used as an indicator of undefined name
      //however, missing name is not taken as an error to allow leaving transitions unnamed if no guards or
      //oracles need to be associated to one
      if (name.length() == 0) {
        name = t.value();
      }
      weight = t.weight();
      if (t.group().length() > 0) {
        group = t.group();
      }
      type = Transition.class.getSimpleName();
      strict = t.strict();
    } else {
      TestStep ts = (TestStep) annotation;
      name = ts.name();
      //same as with transition tag above
      if (name.length() == 0) {
        name = ts.value();
      }
      weight = ts.weight();
      if (ts.group().length() > 0) {
        group = ts.group();
      }
      type = TestStep.class.getSimpleName();
      strict = ts.strict();
    }
    //if no name given, use method name
    if (name.length() == 0) {
      name = parseName(parameters.getMethod().getName());
    }
    TransitionName tName = checkName(name, result, parameters);
    TransitionName groupName = new TransitionName(parameters.getPrefix(), group);
    if (tName == null) {
      return errors;
    }
    createTransition(result, parameters, tName, weight, groupName, strict);

    Method method = parameters.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "@" + type + " methods are not allowed to have parameters: \"" + 
              method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }

    return errors;
  }

  private TransitionName checkName(String name, ParserResult result, ParserParameters parameters) {
    if (name.length() == 0) {
      errors += "Test step must have a name. Define the \"name\" or \"value\" property.\n";
      return null;
    }
    if (name.equals("all")) {
      errors += "Test step name \"all\" is reserved. Choose another.\n";
      return null;
    }
    String prefix = parameters.getPrefix();
    TransitionName tName = new TransitionName(prefix, name);
    if (result.getFsm().getTransition(tName) != null) {
      errors += "Test step name must be unique. '"+tName+"' given several times.\n";
      return null;
    }
    return tName;
  }
  
  private void createTransition(ParserResult result, ParserParameters parameters, 
                                TransitionName name, int weight, TransitionName group, boolean strict) {
    log.debug("creating transition:" + name);
    FSMTransition transition = result.getFsm().createTransition(name, weight);
    transition.setTransition(new InvocationTarget(parameters, TestStep.class));
    transition.setGroupName(group);
    transition.setStrict(strict);
  }

  public static String parseName(String name) {
    //TODO: test with method name of 1 char
    name = Character.toUpperCase(name.charAt(0))+name.substring(1);
    return name;
  }
}

