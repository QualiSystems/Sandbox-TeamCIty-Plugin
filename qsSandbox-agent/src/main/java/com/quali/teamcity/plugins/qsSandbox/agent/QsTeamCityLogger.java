package com.quali.teamcity.plugins.qsSandbox.agent;

import com.quali.cloudshell.QsLogger;
import jetbrains.buildServer.agent.SimpleBuildLogger;

public class QsTeamCityLogger extends QsLogger {

    private final SimpleBuildLogger listener;

    public QsTeamCityLogger(SimpleBuildLogger listener) {
        this.listener = listener;
    }

    @Override
    public void Debug(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void Info(String s) {
        listener.message(s);
    }

    @Override
    public void Error(String s) { listener.error(s);}
}
