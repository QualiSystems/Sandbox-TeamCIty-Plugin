package com.quali.teamcity.plugins.qsSandbox.agent.runner;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import org.jetbrains.annotations.NotNull;
import com.quali.teamcity.plugins.qsSandbox.common.Constants;

public class SandboxRunnerAgentBuildRunner implements AgentBuildRunner {


    @NotNull
    @Override
    public BuildProcess createBuildProcess(@NotNull final AgentRunningBuild agentRunningBuild, @NotNull BuildRunnerContext buildRunnerContext) throws RunBuildException {

        return new SandboxBuildProcess(agentRunningBuild, buildRunnerContext);
    }

    @NotNull
    @Override
    public AgentBuildRunnerInfo getRunnerInfo() {
        return new AgentBuildRunnerInfo() {
            @NotNull
            @Override
            public String getType() {
                return Constants.PLUGIN_NAME;
            }

            @Override
            public boolean canRun(@NotNull BuildAgentConfiguration buildAgentConfiguration) {
                return true;
            }
        };
    }
}
