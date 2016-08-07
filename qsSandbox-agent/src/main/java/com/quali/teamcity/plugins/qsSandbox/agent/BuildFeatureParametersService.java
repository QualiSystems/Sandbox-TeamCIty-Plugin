package com.quali.teamcity.plugins.qsSandbox.agent;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface BuildFeatureParametersService {
  @NotNull
  List<String> getBuildFeatureParameters(@NotNull final String buildFeatureType, @NotNull final String parameterName);
}