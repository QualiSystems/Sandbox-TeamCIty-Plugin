<%@ include file="/include.jsp"%>

<bs:linkCSS dynamic="${true}">
   /css/admin/adminMain.css
   /css/admin/serverConfig.css
</bs:linkCSS>

<bs:linkScript>
    /js/bs/testConnection.js
    /plugins/qsSandbox/js/sandboxSettings.js
</bs:linkScript>

<script type="text/javascript">
    $j(function() {
        SandboxAdmin.SettingsForm.setupEventHandlers();
    });
</script>

<jsp:useBean id="sandboxSettings"
             scope="request"
             type="com.quali.teamcity.plugins.qsSandbox.server.admin.QsSandboxSettingsBean"/>

 <c:url value="/qsSandbox/adminSettings.html" var="actionUrl" />

<div id="settingsContainer">
  <form action="${actionUrl}" id="sandboxAdminForm" method="post" onsubmit="return SandboxAdmin.SettingsForm.submitSettings()" autocomplete="off">
    <div>
          <c:choose>
            <c:when test="${sandboxSettings.disabled}">
              <div class="headerNote">
			  <span class="icon icon16 build-status-icon build-status-icon_paused"></span>
                The CloudShell Sandbox plugin is <strong>disabled</strong>. All CloudShell Sandbox operations are suspended&nbsp;&nbsp;<a class="btn btn_mini" href="#" id="enable-btn">Enable</a>
              </div>
            </c:when>
            <c:otherwise>
              <div style="margin-left: 0.6em;">
                The CloudShell Sandbox plugin is <strong>enabled</strong>&nbsp;&nbsp;<a class="btn btn_mini" href="#" id="disable-btn">Disable</a>
              </div>
            </c:otherwise>
          </c:choose>

          <bs:messages key="message" />
          <br/>
          <div style="cloudshell-config-errors" id="cloudshellErrors">
          </div>

          <table class="runnerFormTable">
                <tr class="groupingTitle">
                        <td colspan="2">General Configuration&nbsp;<a href="http://www.quali.com/" class="helpIcon" style="vertical-align: middle;" target="_blank"><bs:helpIcon/></a></td>
                </tr>
                <tr>
                    <th>
                        <label for="serverAddress">Sandbox API Host Address: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="serverAddress" value="${sandboxSettings.serverAddress}" style="width: 300px;" />
                        <span class="smallNote">CloudShell Sandbox API address and port. By default, the Sandbox API is using port 82.
                                                For Example: http://192.168.1.1:82 or https://10.10.19.1:82</span>
                        <span class="error" id="errorServerAddress"></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="username">User Name: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="username" value="${sandboxSettings.username}" style="width: 300px;" />
                        <span class="smallNote">The CloudShell user name to use. This user will be used to authenticate through the Sandbox API.</span>
                        <span class="error" id="errorUsername"></span>
                    </td>
                </tr>

                <tr>
                    <th>
                        <label for="password">Password: <l:star /></label>
                    </th>
                    <td>
                        <forms:passwordField name="password" encryptedPassword="${sandboxSettings.password}" style="width: 300px;"/>
                        <span class="fieldExplanation">CloudShell password of the given user, In order to authenticate through the Sandbox API.</span>
                        <span class="error" id="errorPassword"></span>

                    </td>
                </tr>


                <tr>
                    <th>
                        <label for="domain">domain: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="domain" value="${sandboxSettings.domain}" style="width: 300px;" />
                        <span class="smallNote">CloudShell domain of the given user, In order to authenticate through the Sandbox API.</span>
                        <span class="error" id="errorDomain"></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="ignoreSsl">Ignore SSL Certificate: <l:star /></label>
                    </th>
                    <td>
                        <forms:checkbox name="ignoreSsl" checked="${sandboxSettings.ignoreSsl}"/>
                        <span class="smallNote">If HTTPS communication is enabled in CloudShell Sandbox API without a singed certificate, check this option to ignore the certificate.</span>
                    </td>
                </tr>
           </table>

            <div class="saveButtonsBlock">
                <forms:submit type="submit" label="Save" />
                <forms:submit id="testConnection" type="button" label="Test Connection"/>
                <input type="hidden" id="submitSettings" name="submitSettings" value="store"/>
                <input type="hidden" id="publicKey" name="publicKey"
                       value="<c:out value='${sandboxSettings.hexEncodedPublicKey}'/>"/>
                <forms:saving />
            </div>
    </div>
  </form>

    <bs:dialog dialogId="testConnectionDialog"
               title="Test Connection"
               closeCommand="BS.TestConnectionDialog.close();"
               closeAttrs="showdiscardchangesmessage='false'">
        <div id="testConnectionStatus"></div>
        <div id="testConnectionDetails" class="mono"></div>
    </bs:dialog>
    <forms:modified/>
</div>