package com.quali.teamcity.plugins.qsSandbox.server.admin;

import com.quali.cloudshell.SandboxApiGateway;
import com.quali.teamcity.plugins.qsSandbox.common.Constants;
import com.quali.teamcity.plugins.qsSandbox.server.SandboxAdminSettings;
import jetbrains.buildServer.controllers.*;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SandboxSettingsController extends BaseFormXmlController {

    private static final String CONTROLLER_PATH = "/qsSandbox/adminSettings.html";

    //In use in the sandboxAdminSetting.jsp UI
    public static final String TEST_COMPLETED_SUCCESSFULLY = "Test completed successfully";

    private SandboxAdminSettings sandboxAdminSettings;

    public SandboxSettingsController(@NotNull WebControllerManager manager,
                                     SandboxAdminSettings sandboxAdminSettings,
                                     PluginDescriptor descriptor){

        this.sandboxAdminSettings = sandboxAdminSettings;
        manager.registerController(CONTROLLER_PATH, this);
    }

    @Override
    protected ModelAndView doGet(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    protected void doPost(@NotNull HttpServletRequest request, @NotNull HttpServletResponse httpServletResponse, @NotNull Element xmlResponse) {

        String action = request.getParameter("action");
        if (action != null) {
            boolean pause = "disable".equals(action);
            sandboxAdminSettings.setDisabled(!pause);
            try {
                sandboxAdminSettings.saveProperties();
            } catch (IOException e) {
                //TODO: handle IOException
            }
            return;
        }

        if (PublicKeyUtil.isPublicKeyExpired(request)) {
            PublicKeyUtil.writePublicKeyExpiredError(xmlResponse);
            return;
        }
        QsSandboxSettingsBean bean = new QsSandboxSettingsBean(sandboxAdminSettings.getServerAddress(),
                sandboxAdminSettings.getUsername(),
                sandboxAdminSettings.getPassword(),
                sandboxAdminSettings.getDomain(),
                sandboxAdminSettings.getIgnoreSsl(),
                sandboxAdminSettings.getDisabled());

        FormUtil.bindFromRequest(request, bean);

        String submitSettings = request.getParameter("submitSettings");
        if (submitSettings.equals("storeInSession")){
            XmlResponseUtil.writeFormModifiedIfNeeded(xmlResponse, bean);
        } else {
            ActionErrors errors = validate(bean);
            if (errors.hasNoErrors()) {
                if (submitSettings.equals("testConnection")) {
                    String testResult = handleTestNotification(bean);
                    XmlResponseUtil.writeTestResult(xmlResponse, testResult);
                } else if (submitSettings.equals("store")) {
                    try {
                        sandboxAdminSettings.setIgnoreSsl(bean.getIgnoreSsl());
                        sandboxAdminSettings.setDomain(bean.getDomain());
                        sandboxAdminSettings.setPassword(bean.getPassword());
                        sandboxAdminSettings.setServerAddress(bean.getServerAddress());
                        sandboxAdminSettings.setUsername(bean.getUsername());
                        sandboxAdminSettings.saveProperties();
                    } catch (IOException e) {
                        //TODO: handle IOException
                    }
                    FormUtil.removeAllFromSession(request.getSession(), bean.getClass());
                    writeRedirect(xmlResponse, request.getContextPath() +
                            "/admin/admin.html?item=" + Constants.PLUGIN_NAME);
                }
            }
            writeErrors(xmlResponse, errors);
        }
    }

    private ActionErrors validate(@NotNull QsSandboxSettingsBean settings) {
        ActionErrors errors = new ActionErrors();
        if (jetbrains.buildServer.util.StringUtil.isEmptyOrSpaces(settings.getServerAddress())) {
            errors.addError("emptyServerAddress", "Server address must not be empty");
        }
        if (jetbrains.buildServer.util.StringUtil.isEmptyOrSpaces(settings.getUsername())) {
            errors.addError("emptyUsername", "User name must not be empty");
        }
        if (jetbrains.buildServer.util.StringUtil.isEmptyOrSpaces(settings.getPassword())) {
            errors.addError("emptyPassword", "Password must not be empty");
        }
        if (jetbrains.buildServer.util.StringUtil.isEmptyOrSpaces(settings.getDomain())) {
            errors.addError("emptyDomain", "Domain must not be empty");
        }
            return errors;
    }

    private String handleTestNotification(QsSandboxSettingsBean sandboxBean){
        SandboxApiGateway gateway = null;
        try {
            gateway = new SandboxApiGateway(sandboxBean.getServerAddress(),
                    sandboxBean.getUsername(),
                    sandboxBean.getPassword(),
                    sandboxBean.getDomain(),
                    sandboxBean.isIgnoreSsl(),
                    null); //TODO: handle logger
            gateway.TryLogin();
            return TEST_COMPLETED_SUCCESSFULLY;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
