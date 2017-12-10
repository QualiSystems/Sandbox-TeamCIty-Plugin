package com.quali.teamcity.plugins.sandbox.server.admin;

import com.quali.teamcity.plugins.sandbox.common.Constants;
import org.jetbrains.annotations.NotNull;

public class QsSandboxBean {
  @NotNull
  public String getQsSandboxAction() {
    return Constants.ACTION;
  }

  @NotNull
  public String getQsSandboxId() {
    return Constants.SANDBOX_ID;
  }

  @NotNull
  public String getQsSandboxBlueprintNameKey() {
    return Constants.BLUEPRINT_VAR;
  }

  @NotNull
  public String getQsSandboxDurationKey() {
    return Constants.DURATION_VAR;
  }

  @NotNull
  public String getQsSandboxTimeoutDurationKey() {
    return Constants.TIMEOUT_DURATION_VAR;
  }

  @NotNull
  public String getQsSandboxParamKey() {
    return Constants.PARAMS_VAR;
  }

}

