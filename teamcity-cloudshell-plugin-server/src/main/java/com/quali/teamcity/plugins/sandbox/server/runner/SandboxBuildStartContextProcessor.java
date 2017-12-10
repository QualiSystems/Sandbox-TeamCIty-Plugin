package com.quali.teamcity.plugins.sandbox.server.runner;

import com.quali.teamcity.plugins.sandbox.common.Constants;
import com.quali.teamcity.plugins.sandbox.server.SandboxAdminSettings;
import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import org.jetbrains.annotations.NotNull;

public class SandboxBuildStartContextProcessor implements BuildStartContextProcessor {
  private ExtensionHolder extensionHolder;
  private SandboxAdminSettings sandboxAdminSettings;

  public SandboxBuildStartContextProcessor(@NotNull final ExtensionHolder extensionHolder, SandboxAdminSettings sandboxAdminSettings) {
    this.extensionHolder = extensionHolder;
    this.sandboxAdminSettings = sandboxAdminSettings;
  }

  @Override
  public void updateParameters(@NotNull final BuildStartContext buildStartContext) {
    buildStartContext.addSharedParameter(Constants.SERVER_VAR, sandboxAdminSettings.getServerAddress());
    buildStartContext.addSharedParameter(Constants.PASSWORD_VAR, sandboxAdminSettings.getPassword());
    buildStartContext.addSharedParameter(Constants.USER_VAR, sandboxAdminSettings.getUsername());
    buildStartContext.addSharedParameter(Constants.DOMAIN_VAR, sandboxAdminSettings.getDomain());
    buildStartContext.addSharedParameter(Constants.IGNORE_SSL, Boolean.toString(sandboxAdminSettings.getIgnoreSsl()));
  }

  public void register() {
    extensionHolder.registerExtension(BuildStartContextProcessor.class, this.getClass().getName(), this);
  }
}
