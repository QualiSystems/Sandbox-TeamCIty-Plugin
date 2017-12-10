package com.quali.teamcity.plugins.sandbox.agent;

import com.quali.cloudshell.logger.QsLogger;
import jetbrains.buildServer.agent.SimpleBuildLogger;

public class QsTeamCityLogger extends QsLogger {

    private final SimpleBuildLogger listener;

    public QsTeamCityLogger(SimpleBuildLogger listener) {
        this.listener = listener;
    }

    @Override
    public void debug(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(String s) {
        listener.message(s);
    }

    @Override
    public void error(String s) { listener.error(s);}
}
