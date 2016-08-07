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

        logger.Info("CloudShell: Starting sandbox...");
        String sandboxId = StartBlueprint(feature, runningBuild);
        runningBuild.addSharedEnvironmentVariable("SANDBOX_ID", sandboxId);
        logger.Info("CloudShell: Sandbox was added and ready for use");
      }
    }
  }

  private String StartBlueprint(AgentBuildFeature feature, AgentRunningBuild runningBuild) {
    String blueprint = URLEncoder.encode(feature.getParameters().get("qs_sandbox_blueprint"));
    int duration = Integer.parseInt(feature.getParameters().get("qs_sandbox_duration"));
    String sandbox = null;
    try {
      sandbox = StartSandBox(server, blueprint, duration, false);
    } catch (SandboxApiException e) {
      e.printStackTrace();
      logger.Error("Start blueprint throw an error, aborting build process...");
      runningBuild.stopBuild(e.getMessage());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    try {
      WaitForSetup(server, sandbox);
    } catch (SandboxApiException e) {
      e.printStackTrace();
      try {
        if(e.getMessage().contains("Error")) {
          logger.Error("Error during sandbox setup, aborting build process...");
          StopSandbox(sandbox, true, server.ignoreSSL);
          sandboxes.remove(sandbox);
          runningBuild.stopBuild(e.getMessage());
        }
      } catch (NoSuchAlgorithmException e1) {
        e1.printStackTrace();
      } catch (KeyStoreException e1) {
        e1.printStackTrace();
      } catch (KeyManagementException e1) {
        e1.printStackTrace();
      } catch (SandboxApiException e1) {
        e1.printStackTrace();
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void WaitForSetup(QsServerDetails qsServerDetails, String sandboxId) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    SandboxApiGateway gateway = new SandboxApiGateway(logger, qsServerDetails);
    gateway.WaitForSandBox(sandboxId, "Ready", 300,qsServerDetails.ignoreSSL);
  }

  private String StartSandBox(QsServerDetails qsServerDetails, String blueprint, int sandboxDuration, boolean isSync) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnsupportedEncodingException {
    SandboxApiGateway gateway = new SandboxApiGateway(logger, qsServerDetails);
    String sandboxId = gateway.StartBlueprint(blueprint, sandboxDuration, isSync, null);
    sandboxes.add(sandboxId);
    return sandboxId;
  }

  public void StopSandbox(String sandboxId, boolean waitForComplete, boolean ignoreSSL) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException {
    SandboxApiGateway gateway = new SandboxApiGateway(logger, server);
    gateway.StopSandbox(sandboxId, true);
  }

  @Override
  public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
    Collection<AgentBuildFeature> buildFeatures = build.getBuildFeatures();
    for (AgentBuildFeature feature : buildFeatures) {
      if (feature.getType().equals(Constants.BUILD_FEATURE_TYPE.toString())) {
        for (String sandbox : sandboxes) {
          try {
            logger.Info("CloudShell: Sandbox stopping: " + sandbox);
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
            logger.Error(e.getMessage());
          }
        }
      }
    }
  }
}
