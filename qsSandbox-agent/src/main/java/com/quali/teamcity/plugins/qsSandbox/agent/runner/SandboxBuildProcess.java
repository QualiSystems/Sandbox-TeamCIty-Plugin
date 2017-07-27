package com.quali.teamcity.plugins.qsSandbox.agent.runner;

import com.quali.cloudshell.QsServerDetails;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import com.quali.teamcity.plugins.qsSandbox.agent.QsTeamCityLogger;
import com.quali.teamcity.plugins.qsSandbox.agent.SandboxActions;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import org.jetbrains.annotations.NotNull;
import com.quali.teamcity.plugins.qsSandbox.common.Constants;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SandboxBuildProcess implements BuildProcess {
    private final AgentRunningBuild runningBuild;
    private final BuildRunnerContext buildRunnerContext;
    final BuildProgressLogger logger;

    public SandboxBuildProcess(@NotNull AgentRunningBuild build, @NotNull BuildRunnerContext context){
        runningBuild = build;
        buildRunnerContext = context;
        logger = build.getBuildLogger();
    }

    @Override
    public void start() throws RunBuildException {
        QsTeamCityLogger qsLogger = new QsTeamCityLogger(logger);

        QsServerDetails server = new QsServerDetails(runningBuild.getSharedConfigParameters().get(Constants.SERVER_VAR),
                runningBuild.getSharedConfigParameters().get(Constants.USER_VAR),
                runningBuild.getSharedConfigParameters().get(Constants.PASSWORD_VAR),
                runningBuild.getSharedConfigParameters().get(Constants.DOMAIN_VAR),
                Boolean.parseBoolean(runningBuild.getSharedConfigParameters().get(Constants.IGNORE_SSL)));

        SandboxActions sandboxActions = new SandboxActions(qsLogger, server);
        Map<String, String> params = buildRunnerContext.getRunnerParameters();

        if (params.get(Constants.ACTION).equals(Constants.START_SANDBOX)) {
            String sandboxId = sandboxActions.StartBlueprint(server,
                    runningBuild,
                    params.get(Constants.BLUEPRINT_VAR),
                    Integer.parseInt(params.get(Constants.DURATION_VAR)),
                    Integer.parseInt(params.get(Constants.TIMEOUT_DURATION_VAR)),
                    params.get(Constants.PARAMS_VAR));

            buildRunnerContext.getBuild().addSharedConfigParameter(Constants.SANDBOX_ID_CONF, sandboxId);

            qsLogger.info("CloudShell: Sandbox was added and ready for use");
        }
        else if (params.get(Constants.ACTION).equals(Constants.STOP_SANDBOX)) {
            try {
                String sandboxId = buildRunnerContext.getConfigParameters().get("SANDBOX_ID");
                sandboxActions.StopSandbox(server, sandboxId, true);
                buildRunnerContext.getBuild().addSharedConfigParameter(Constants.SANDBOX_ID_CONF, "");

            } catch (NoSuchAlgorithmException | IOException | SandboxApiException | KeyManagementException | KeyStoreException e) {
                e.printStackTrace();
                qsLogger.error("CloudShell: Stop Sandbox throw an error: " + e.toString());
            }
        }
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void interrupt() {
    }

    @NotNull
    @Override
    public BuildFinishedStatus waitFor() throws RunBuildException {
        return BuildFinishedStatus.FINISHED_SUCCESS;
    }
}