package com.quali.teamcity.plugins.sandbox.agent;

import com.quali.cloudshell.QsServerDetails;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import com.quali.teamcity.plugins.sandbox.common.Constants;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SandboxPropertiesExtension extends AgentLifeCycleAdapter {
  private final List<String> sandboxes;
  private QsTeamCityLogger logger;
  private QsServerDetails server;

  public SandboxPropertiesExtension(
          @NotNull final EventDispatcher<AgentLifeCycleListener> events) {
    events.addListener(this);
    sandboxes = new ArrayList<>();
  }

  @Override
  public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
    Collection<AgentBuildFeature> buildFeatures = runningBuild.getBuildFeatures();
    for (AgentBuildFeature feature : buildFeatures) {
      logger = new QsTeamCityLogger(runningBuild.getBuildLogger());
      if (feature.getType().equals(Constants.BUILD_FEATURE_TYPE)) {
        logger.info("CloudShell: Using CloudShell Sandbox build feature...");
        server = new QsServerDetails(runningBuild.getSharedConfigParameters().get(Constants.SERVER_VAR),
                runningBuild.getSharedConfigParameters().get(Constants.USER_VAR),
                runningBuild.getSharedConfigParameters().get(Constants.PASSWORD_VAR),
                runningBuild.getSharedConfigParameters().get(Constants.DOMAIN_VAR),
                Boolean.parseBoolean(runningBuild.getSharedConfigParameters().get(Constants.IGNORE_SSL)));

        String blueprint = URLEncoder.encode(feature.getParameters().get(Constants.BLUEPRINT_VAR));
        int duration = Integer.parseInt(feature.getParameters().get(Constants.DURATION_VAR));
        int timouet_duration_min = Integer.parseInt(feature.getParameters().get(Constants.TIMEOUT_DURATION_VAR));
        String params = feature.getParameters().get(Constants.PARAMS_VAR);

        SandboxActions sandboxActions = new SandboxActions(logger, server);
        String sandbox = sandboxActions.StartBlueprint(server, runningBuild, blueprint, duration, timouet_duration_min, params);
        runningBuild.addSharedConfigParameter(Constants.SANDBOX_ID_CONF, sandbox);
        sandboxes.add(sandbox);
        logger.info("CloudShell: Sandbox was added and ready for use");
      }
    }
  }

  @Override
  public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
    Collection<AgentBuildFeature> buildFeatures = build.getBuildFeatures();
    for (AgentBuildFeature feature : buildFeatures) {
      if (feature.getType().equals(Constants.BUILD_FEATURE_TYPE)) {
        for (String sandbox : sandboxes) {
          try {
            SandboxActions sandboxActions = new SandboxActions(logger, server);
            sandboxActions.StopSandbox(server, sandbox, true);
            sandboxes.remove(sandbox);
          } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException | SandboxApiException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
          }
        }
      }
    }
  }
}
