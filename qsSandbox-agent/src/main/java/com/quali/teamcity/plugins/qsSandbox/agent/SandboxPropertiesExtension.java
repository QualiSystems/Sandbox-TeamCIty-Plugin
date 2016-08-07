package com.quali.teamcity.plugins.qsSandbox.agent;

import com.intellij.openapi.diagnostic.Logger;
import com.quali.cloudshell.QsExceptions.SandboxApiException;
import com.quali.cloudshell.QsServerDetails;
import com.quali.cloudshell.SandboxApiGateway;
import jetbrains.buildServer.agent.*;
import com.quali.teamcity.plugins.qsSandbox.common.Constants;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SandboxPropertiesExtension extends AgentLifeCycleAdapter {
  private static final Logger LOG = Logger.getInstance(SandboxPropertiesExtension.class.getName());
  private final List<String> sandboxes;
  private QsTeamCityLogger logger;
  private QsServerDetails server;

  public SandboxPropertiesExtension(
          @NotNull final EventDispatcher<AgentLifeCycleListener> events) {
    events.addListener(this);
    sandboxes = new ArrayList<String>();
  }

  private String StartSandBox(QsServerDetails qsServerDetails, String blueprint, int sandboxDuration) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnsupportedEncodingException {
    SandboxApiGateway gateway = new SandboxApiGateway(logger, qsServerDetails);
    String sandboxId = gateway.startBlueprint(blueprint, sandboxDuration, true, null);
    sandboxes.add(sandboxId);
    return sandboxId;
  }

  @Override
  public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
    Collection<AgentBuildFeature> buildFeatures = runningBuild.getBuildFeatures();
    for (AgentBuildFeature feature : buildFeatures) {
      logger = new QsTeamCityLogger(runningBuild.getBuildLogger());

      if (feature.getType().equals(Constants.BUILD_FEATURE_TYPE.toString())) {
        server = new QsServerDetails(feature.getParameters().get("qs_sandbox_server"),
                feature.getParameters().get("qs_sandbox_user"),
                feature.getParameters().get("secure:qs_sandbox_password"),
                feature.getParameters().get("qs_sandbox_domain"),
                true);

        logger.Info("CloudShell Sandbox Started");
        String sandboxId = StartBlueprint(feature);
        runningBuild.addSharedEnvironmentVariable("SANDBOX_ID", sandboxId);
        logger.Info("CloudShell Sandbox Finished");
      }
    }
  }

  private String StartBlueprint(AgentBuildFeature feature) {
    String blueprint = URLEncoder.encode(feature.getParameters().get("qs_sandbox_blueprint"));
    int duration = Integer.parseInt(feature.getParameters().get("qs_sandbox_duration"));
    try {
      return StartSandBox(server, blueprint, duration);
    } catch (com.quali.cloudshell.QsExceptions.SandboxApiException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void StopSandbox(String sandboxId, boolean waitForComplete, boolean ignoreSSL) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException {
    SandboxApiGateway gateway = new SandboxApiGateway(logger, server);
    gateway.StopSandbox(sandboxId, true);
  }

  @Override
  public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
    logger.Info("CloudShell: beforeBuildFinish");
    Collection<AgentBuildFeature> buildFeatures = build.getBuildFeatures();
    for (AgentBuildFeature feature : buildFeatures) {
      if (feature.getType().equals(Constants.BUILD_FEATURE_TYPE.toString())) {
        for (String sandbox : sandboxes) {
          try {
            logger.Info("CloudShell: beforeBuildFinish: Stopping: " + sandbox);
            StopSandbox(sandbox, true, true);
            sandboxes.remove(sandbox);
          } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
          } catch (KeyStoreException e) {
            e.printStackTrace();
          } catch (KeyManagementException e) {
            e.printStackTrace();
          } catch (SandboxApiException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
