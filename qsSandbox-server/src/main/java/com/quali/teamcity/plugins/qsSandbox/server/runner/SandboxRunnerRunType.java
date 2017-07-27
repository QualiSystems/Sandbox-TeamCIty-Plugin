package com.quali.teamcity.plugins.qsSandbox.server.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.quali.teamcity.plugins.qsSandbox.common.Constants;
import com.quali.teamcity.plugins.qsSandbox.server.admin.QsSandboxBean;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SandboxRunnerRunType extends RunType {

  private String viewRunnerJspPath;
  private String runnerJspPath;
  private QsSandboxBean sandboxBean;

  public SandboxRunnerRunType(final RunTypeRegistry runTypeRegistry, PluginDescriptor descriptor, QsSandboxBean sandboxBean) {
    this.runnerJspPath = descriptor.getPluginResourcesPath("sandboxRunnerParams.jsp");
    this.viewRunnerJspPath = descriptor.getPluginResourcesPath("viewSandboxRunnerParams.jsp");
    this.sandboxBean = sandboxBean;
    runTypeRegistry.registerRunType(this);
  }

  @Override
  @Nullable
  public PropertiesProcessor getRunnerPropertiesProcessor() {
    return new ParametersValidator();
  }

  @Override
  public String getEditRunnerParamsJspFilePath() {
    return runnerJspPath;
  }

  @Override
  public String getViewRunnerParamsJspFilePath() {
    return viewRunnerJspPath;
  }

  @Override
  public Map<String, String> getDefaultRunnerProperties() {

    HashMap<String, String> params = new HashMap<String, String>();
    this.setupDefaultProperties(params);

    return null;
  }

  private void setupDefaultProperties(Map<String, String> params) {
//    if (sandboxBean != null) {
//      params.remove(Constants.BLUEPRINT_VAR);
//      params.put(Constants.BLUEPRINT_VAR, sandboxBean.getQsSandboxBlueprintNameKey());
//  }
  }

  @NotNull
  @Override
  public String getDescription() {
    return "This build feature allows running build configuration attached to a CloudShell Sandbox.";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return Constants.RUNNER_DISPLAY_NAME;
  }

  @NotNull
  @Override
  public String getType() {
    return Constants.PLUGIN_NAME;
  }

  @NotNull
  @Override
  public String describeParameters(@NotNull final Map<String, String> parameters) {
    StringBuilder result = new StringBuilder();
    if (parameters.get(Constants.ACTION).equals(Constants.START_SANDBOX)){
      result.append("CloudShell Start Sandbox from blueprint:  ").append(StringUtil.emptyIfNull(parameters.get(sandboxBean.getQsSandboxBlueprintNameKey())));
    }
    else if (parameters.get(Constants.ACTION).equals(Constants.STOP_SANDBOX)){
      result.append("CloudShell Stop Sandbox");
    }
    return result.append("\n").toString();
  }

  static class ParametersValidator implements PropertiesProcessor {

    public Collection<InvalidProperty> process(final Map<String, String> properties) {
      final Collection<InvalidProperty> result = new ArrayList<InvalidProperty>(1);
      if ((properties.get(Constants.ACTION)).equals(Constants.START_SANDBOX)) {
        if (StringUtil.isEmpty(properties.get(Constants.BLUEPRINT_VAR))) {
          result.add(new InvalidProperty(Constants.BLUEPRINT_VAR, "Blueprint name must be provided."));
        }
        if (StringUtil.isEmpty(properties.get(Constants.DURATION_VAR))) {
          result.add(new InvalidProperty(Constants.DURATION_VAR, "Sandbox duration must be provided."));
        }
        if (!StringUtil.isEmpty(properties.get(Constants.DURATION_VAR)) &&
                !NumberUtils.isNumber(properties.get(Constants.DURATION_VAR))) {
          result.add(new InvalidProperty(Constants.DURATION_VAR, "Numeric value must be provide for Sandbox duration."));
        }
        if (StringUtil.isEmpty(properties.get(Constants.TIMEOUT_DURATION_VAR))) {
          result.add(new InvalidProperty(Constants.TIMEOUT_DURATION_VAR, "Sandbox timeout duration must be provided."));
        }
        if (!StringUtil.isEmpty(properties.get(Constants.TIMEOUT_DURATION_VAR)) &&
                !NumberUtils.isNumber(properties.get(Constants.TIMEOUT_DURATION_VAR))) {
          result.add(new InvalidProperty(Constants.TIMEOUT_DURATION_VAR, "Numeric value must be provide for Sandbox timeout duration."));
        }
      } else if ((properties.get(Constants.ACTION)).equals(Constants.STOP_SANDBOX)){
        if (StringUtil.isEmpty(properties.get(Constants.SANDBOX_ID))) {
          result.add(new InvalidProperty(Constants.SANDBOX_ID, "Sandbox Id or %env.SANDBOX_ID% must be provided. "));
        }
      }
      return result;
    }
  }
}
