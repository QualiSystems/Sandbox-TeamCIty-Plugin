package com.quali.teamcity.plugins.qsSandbox.server.admin;


import com.intellij.openapi.util.text.StringUtil;
import com.quali.cloudshell.SandboxApiGateway;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import com.quali.teamcity.plugins.qsSandbox.server.SandboxAdminSettings;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class SandboxSettingsController extends BaseController {

    private static final String CONTROLLER_PATH = "/qsSandbox/adminSettings.html";
    public static final String EDIT_PARAMETER = "edit";
    private static final Object ACTION_ENABLE = "enable";
    private static final String ACTION_PARAMETER = "action";
    public static final String TEST_PARAMETER = "test";
    public static final String MESSAGES = "messages";

    //String is in use in the sandboxAdminSetting.jsp UI
    public static final String TEST_COMPLETED_SUCCESSFULLY = "Test completed successfully";

    private String serverAddress;
    private String username;
    private String domain;
    private boolean ignoreSsl;
    private String password;
    private boolean disabled;

    private SandboxAdminSettings sandboxAdminSettings;
    private PluginDescriptor descriptor;

    public SandboxSettingsController(@NotNull WebControllerManager manager,
                                     SandboxAdminSettings sandboxAdminSettings,
                                     PluginDescriptor descriptor){

        this.sandboxAdminSettings = sandboxAdminSettings;
        this.descriptor = descriptor;
        manager.registerController(CONTROLLER_PATH, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();

        if(request.getParameter(EDIT_PARAMETER) != null){
            params = this.handleConfigurationChange(request);
        }
        else if(request.getParameter(TEST_PARAMETER) != null) {
            params = this.handleTestNotification(request);
        }
        else if (request.getParameter(ACTION_PARAMETER) != null) {
            this.handlePluginStatusChange(request);
        }
        return new ModelAndView(descriptor.getPluginResourcesPath() + "ajaxEdit.jsp", params);
    }

    private HashMap<String, Object> handleTestNotification(HttpServletRequest request) throws IOException, CloudShellConfigValidationException, SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        updateRequestParams(request);
        HashMap<String, Object> params = new HashMap<String, Object>();

        try {
            Validate(serverAddress, username, domain, password);
        } catch (CloudShellConfigValidationException e)
        {
            params.put(MESSAGES, e.getMessage());
            return params;
        }

        SandboxApiGateway gateway = new SandboxApiGateway(serverAddress, username, password, domain, ignoreSsl, null);
        try {
            gateway.TryLogin();
            params.put(MESSAGES, TEST_COMPLETED_SUCCESSFULLY);
        } catch (Exception e){
            params.put(MESSAGES, e.getMessage());
        }

        return params;
    }


    private void handlePluginStatusChange(HttpServletRequest request) throws IOException {
        Boolean disabled = request.getParameter(ACTION_PARAMETER).equals(ACTION_ENABLE);
        sandboxAdminSettings.setDisabled(disabled);
        sandboxAdminSettings.saveProperties();
    }

    private void updateRequestParams(HttpServletRequest request) {
        serverAddress = request.getParameter("serverAddress");
        username = request.getParameter("username");
        password = request.getParameter("password");
        domain = request.getParameter("domain");
        ignoreSsl = Boolean.valueOf(request.getParameter("ignoreSsl"));
        disabled = Boolean.valueOf(request.getParameter("disabled"));
    }


    private void Validate(String serverAddress, String username, String domain, String password
            ) throws CloudShellConfigValidationException {
        if(isNullOrEmpty(serverAddress)
                || isNullOrEmpty(username)
                || isNullOrEmpty(domain)
                || isNullOrEmpty(password)){

            throw new CloudShellConfigValidationException("Could not validate parameters. Please recheck the request.");
        }
    }

    private boolean isNullOrEmpty(String str){
        return str == null || StringUtil.isEmpty(str);
    }

    private HashMap<String, Object> handleConfigurationChange(HttpServletRequest request) throws IOException, CloudShellConfigValidationException {
        HashMap<String, Object> params = new HashMap<String, Object>();

        updateRequestParams(request);

        try {
            Validate(serverAddress, username, domain, password);
        } catch (CloudShellConfigValidationException e)
        {
            params.put("message", e.getMessage());
            return params;
        }

        sandboxAdminSettings.setIgnoreSsl(ignoreSsl);
        sandboxAdminSettings.setDomain(domain);
        sandboxAdminSettings.setPassword(password);
        sandboxAdminSettings.setServerAddress(serverAddress);
        sandboxAdminSettings.setUsername(username);
        sandboxAdminSettings.saveProperties();

        params.put("message", "Saved");
        return params;
    }

    public class CloudShellConfigValidationException extends Exception {
        public CloudShellConfigValidationException(String message) {
            super(message);
        }
    }
}
