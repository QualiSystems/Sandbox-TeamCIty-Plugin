package com.quali.teamcity.plugins.qsSandbox.server.admin;

import com.quali.teamcity.plugins.qsSandbox.common.Constants;
import com.quali.teamcity.plugins.qsSandbox.server.SandboxAdminSettings;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

public class SandboxAdminPage extends AdminPage {
    private static final String PAGE = "sandboxAdminSettings.jsp";
    private static final String TAB_TITLE = "CloudShell Sandbox";
    private final String jspHome;
    private SandboxAdminSettings sandboxAdminSettings;

    protected SandboxAdminPage(@NotNull PagePlaces pagePlaces,
                               @NotNull PluginDescriptor descriptor,
                               SandboxAdminSettings sandboxAdminSettings) {
        super(pagePlaces);
        this.sandboxAdminSettings = sandboxAdminSettings;

        setPluginName(Constants.PLUGIN_NAME);
        setIncludeUrl(descriptor.getPluginResourcesPath(PAGE));
        jspHome = descriptor.getPluginResourcesPath();
        setTabTitle(TAB_TITLE);
        ArrayList<String> after = new ArrayList<String>();
        ArrayList<String> before = new ArrayList<String>();
        setPosition(PositionConstraint.between(after, before));
        register();
        Loggers.SERVER.info("CloudShell configuration page registered");
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request){
        super.fillModel(model, request);

        model.put("serverAddress", sandboxAdminSettings.getServerAddress());
        model.put("username", sandboxAdminSettings.getUsername());
        model.put("domain", sandboxAdminSettings.getDomain());
        model.put("ignoreSsl", sandboxAdminSettings.getIgnoreSsl());
        model.put("password", sandboxAdminSettings.getPassword());
        model.put("disabled", !sandboxAdminSettings.getDisabled());
        model.put("jspHome", this.jspHome);
    }

    @NotNull
    @Override
    public String getGroup() {
        return INTEGRATIONS_GROUP;
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest request) {
        return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
    }
}
