package com.quali.teamcity.plugins.sandbox.server.feature;

import com.quali.teamcity.plugins.sandbox.common.Constants;
import com.quali.teamcity.plugins.sandbox.server.admin.QsSandboxBean;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;

public class QsSandboxBuildFeature extends BuildFeature {
  private final String myEditUrl;
  private final QsSandboxBean myBean;

  public QsSandboxBuildFeature(
          @NotNull final QsSandboxBean bean,
          @NotNull final PluginDescriptor descriptor) {

    myBean = bean;
    myEditUrl = descriptor.getPluginResourcesPath("qsSandboxBuildFeature.jsp");
  }

  @NotNull
  @Override
  public String getType() {
    return Constants.BUILD_FEATURE_TYPE;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "CloudShell Sandbox";
  }

  @Nullable
  @Override
  public String getEditParametersUrl() {
    return myEditUrl;
  }

  @NotNull
  @Override
  public String describeParameters(@NotNull Map<String, String> params) {
    final String blueprint = params.get(myBean.getQsSandboxBlueprintNameKey());
    return "Blueprint \"" + blueprint + "\" will be attached to the build configuration" ;
  }

  @Override
  public boolean isMultipleFeaturesPerBuildTypeAllowed() {
    return false;
  }

  @Nullable
  @Override
  public PropertiesProcessor getParametersProcessor() {
    return new PropertiesProcessor() {
      public Collection<InvalidProperty> process(Map<String, String> properties) {
        List<InvalidProperty> result = new ArrayList<InvalidProperty>();

//        final String userName = properties.get(myBean.getQsSandboxUserKey());
//        if (isEmpty(userName))
//          result.add(new InvalidProperty(myBean.getQsSandboxUserKey(), "Please specify an user name"));
//
//        final String password = properties.get(myBean.getQsSandboxPasswordKey());
//        if (isEmpty(password))
//          result.add(new InvalidProperty(myBean.getQsSandboxPasswordKey(), "Please specify a password"));

        final String duration = properties.get(myBean.getQsSandboxDurationKey());
        if (!NumberUtils.isNumber(duration))
          result.add(new InvalidProperty(myBean.getQsSandboxDurationKey(), "Please specify a numeric duration"));

        final String blueprint = properties.get(myBean.getQsSandboxBlueprintNameKey());
        if (isEmpty(blueprint))
          result.add(new InvalidProperty(myBean.getQsSandboxBlueprintNameKey(), "Please specify a blueprint"));

        return result;
      }
    };
  }
}