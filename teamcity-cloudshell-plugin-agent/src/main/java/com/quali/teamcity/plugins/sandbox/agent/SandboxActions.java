package com.quali.teamcity.plugins.sandbox.agent;

import com.quali.cloudshell.QsServerDetails;
import com.quali.cloudshell.SandboxApiGateway;
import com.quali.cloudshell.qsExceptions.InvalidApiCallException;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import jetbrains.buildServer.agent.AgentRunningBuild;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SandboxActions {
    private QsTeamCityLogger logger;
    private QsServerDetails server;

    public SandboxActions(QsTeamCityLogger logger, QsServerDetails server) {
        this.logger = logger;
        this.server = server;
    }

    public void StopSandbox(QsServerDetails server, String sandboxId, boolean waitForComplete) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException, IOException {
        SandboxApiGateway gateway = new SandboxApiGateway(logger, server);
        gateway.StopSandbox(sandboxId, waitForComplete);
        try {
            gateway.VerifyTeardownSucceeded(sandboxId);
        } catch (InvalidApiCallException e) {
            logger.info("Teardown process cannot be verified, please use newer version of CloudShell to support this feature.");
        }
    }

    public String StartBlueprint(QsServerDetails server, AgentRunningBuild runningBuild, String blueprint, int duration, int timouet_duration_min, String params) {

        String sandbox = null;
        try {
            sandbox = StartSandBox(server, blueprint, duration, false, parseParams(params), 300);
        } catch (SandboxApiException e) {
            e.printStackTrace();
            logger.error("Start blueprint throw an error, aborting build process...");
            runningBuild.stopBuild(e.getMessage());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
            e.printStackTrace();
        }
        try {
            WaitForSetup(server, sandbox, timouet_duration_min);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | SandboxApiException | IOException e) {
            logger.error("Start blueprint throw an error, aborting build process...");
            runningBuild.stopBuild(e.getMessage());
        }
        return sandbox;
    }

    private Map<String, String> parseParams(String params) throws SandboxApiException {
        if (params != null && !params.isEmpty()) {
            Map<String, String> map = new HashMap<>();
            String[] parameters = params.split(";");
            for (String param: parameters) {
                String[] split = param.trim().split("=");
                if (split.length < 2) throw new SandboxApiException("Failed to parse blueprint parameters");
                map.put(split[0], split[1]);
            }
            return map;
        }
        return null;
    }

    private void WaitForSetup(QsServerDetails qsServerDetails, String sandboxId, int timouet_duration_min) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException, IOException {
        SandboxApiGateway gateway = new SandboxApiGateway(logger, qsServerDetails);
        gateway.WaitForSandBox(sandboxId, "Ready", timouet_duration_min*60,qsServerDetails.ignoreSSL);
    }

    private String StartSandBox(QsServerDetails qsServerDetails, String blueprint, int sandboxDuration, boolean isSync, Map<String, String> params, int timout) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        SandboxApiGateway gateway = new SandboxApiGateway(logger, qsServerDetails);
        return gateway.TryStartBlueprint(blueprint, sandboxDuration, isSync, null, params, timout);
    }

    private void StopSandbox(String sandboxId, boolean waitForComplete, boolean ignoreSSL) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException, IOException {
        SandboxApiGateway gateway = new SandboxApiGateway(logger, server);
        gateway.StopSandbox(sandboxId, waitForComplete);
    }
}
