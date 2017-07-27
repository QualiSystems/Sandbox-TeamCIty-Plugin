package com.quali.teamcity.plugins.qsSandbox.server;

import com.quali.teamcity.plugins.qsSandbox.common.Constants;
import org.jetbrains.annotations.NotNull;

public class QsSandboxBean {
  @NotNull
  public String getQsSandboxUserKey() {
    return Constants.USER_VAR;
  }

  @NotNull
  public String getQsSandboxPasswordKey() {
    return Constants.PASSWORD_VAR;
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
  public String getQsSandboxParamKey() {
    return Constants.PARAMS_VAR;
  }

  @NotNull
  public String getQsSandboxDomainKey() {
    return Constants.DOMAIN_VAR;
  }

  @NotNull
  public String getQsSandboxServerKey() {
    return Constants.SERVER_VAR;
  }
}

