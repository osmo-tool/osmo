package osmo.tester.ide.intellij;

import com.intellij.ExtensionPoints;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.JUnitPatcher;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.DebuggingRunnerData;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestSearchScope;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.module.LanguageLevelUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.ex.JavaSdkUtil;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.util.PathUtil;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/** @author Teemu Kanstren */
public class OSMORunState extends JavaCommandLineState {
  private static final Logger LOG = Logger.getInstance("OSMO Runner");
  private final OSMORunConfig config;
  private final OSMORunParameters parameters;
  private final RunnerSettings runnerSettings;
  private final ConfigurationPerRunnerSettings myConfigurationPerRunnerSettings;
  private int port;
  private String debugPort;
  private File myTempFile;
  private ServerSocket myServerSocket;
  
  public OSMORunState(@NotNull ExecutionEnvironment environment, OSMORunConfig config) {
    super(environment);
    this.config = config;
    this.parameters = config.getRunParameters();
    this.runnerSettings = environment.getRunnerSettings();
    myConfigurationPerRunnerSettings = environment.getConfigurationSettings();

    if (runnerSettings.getData() instanceof DebuggingRunnerData) {
      DebuggingRunnerData debuggingRunnerData = ((DebuggingRunnerData)runnerSettings.getData());
      debugPort = debuggingRunnerData.getDebugPort();
      if (debugPort.length() == 0) {
        try {
          debugPort = DebuggerUtils.getInstance().findAvailableDebugAddress(true);
        }
        catch (ExecutionException e) {
          LOG.error(e);
        }
        debuggingRunnerData.setDebugPort(debugPort);
      }
      debuggingRunnerData.setLocal(true);
    }
  }

  @Override
  protected JavaParameters createJavaParameters() throws ExecutionException {
    final Project project = config.getProject();
    final JavaParameters javaParameters = new JavaParameters();
    javaParameters.setupEnvs(parameters.getEnvs(), parameters.isPassParentEnvs());
    javaParameters.setMainClass("org.testng.RemoteTestNGStarter");
    javaParameters.setWorkingDirectory(parameters.getWorkingDirectory());
//    javaParameters.getClassPath().add(PathUtil.getJarPathForClass(RemoteTestNGStarter.class));

    //the next few lines are awkward for a reason, using compareTo for some reason causes a JVM class verification error!
    Module module = config.getConfigurationModule().getModule();
    LanguageLevel effectiveLanguageLevel = module == null
            ? LanguageLevelProjectExtension.getInstance(project).getLanguageLevel()
            : LanguageLevelUtil.getEffectiveLanguageLevel(module);
    final boolean is15 = effectiveLanguageLevel != LanguageLevel.JDK_1_4 && effectiveLanguageLevel != LanguageLevel.JDK_1_3;

    LOG.info("Language level is " + effectiveLanguageLevel.toString());
    LOG.info("is15 is " + is15);
    final String pathToBundledJar = PathUtil.getJarPathForClass(AfterClass.class);

    // Configure rest of jars
    JavaParametersUtil.configureConfiguration(javaParameters, parameters);
    Sdk jdk = module == null ? ProjectRootManager.getInstance(project).getProjectSdk() : ModuleRootManager.getInstance(module).getSdk();
    javaParameters.setJdk(jdk);
    final Object[] patchers = Extensions.getExtensions(ExtensionPoints.JUNIT_PATCHER);
    for (Object patcher : patchers) {
      ((JUnitPatcher)patcher).patchJavaParameters(module, javaParameters);
    }
    JavaSdkUtil.addRtJar(javaParameters.getClassPath());

    // Append coverage parameters if appropriate
    for (RunConfigurationExtension ext : Extensions.getExtensions(RunConfigurationExtension.EP_NAME)) {
      ext.updateJavaParameters(config, javaParameters, getRunnerSettings());
    }

    try {
      port = NetUtils.findAvailableSocketPort();
    }
    catch (IOException e) {
      throw new ExecutionException("Unable to bind to port " + port, e);
    }

    try {
      myServerSocket = new ServerSocket(0, 0, InetAddress.getByName("127.0.0.1"));
      javaParameters.getProgramParametersList().add("-socket" + myServerSocket.getLocalPort());
      myTempFile = FileUtil.createTempFile("idea_testng", ".tmp");
      myTempFile.deleteOnExit();
      javaParameters.getProgramParametersList().add("-temp", myTempFile.getAbsolutePath());
    }
    catch (IOException e) {
      LOG.error(e);
    }
    // Configure for debugging
    if (runnerSettings.getData() instanceof DebuggingRunnerData) {
      ParametersList params = javaParameters.getVMParametersList();

      String hostname = "localhost";
      try {
        hostname = InetAddress.getLocalHost().getHostName();
      }
      catch (UnknownHostException e) {
      }
      params.add("-Xdebug");
      params.add("-Xrunjdwp:transport=dt_socket,address=" + hostname + ':' + debugPort + ",suspend=y,server=n");
      //            params.add(debugPort);
    }

    return javaParameters;
  }
}
