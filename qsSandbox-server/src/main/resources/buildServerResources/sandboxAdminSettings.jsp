<%@ include file="/include.jsp"%>

<c:url value="/qsSandbox/adminSettings.html" var="actionUrl" />
<bs:linkCSS dynamic="${true}">
  ${jspHome}css/adminStyles.css
   /css/admin/adminMain.css
   /css/admin/serverConfig.css
</bs:linkCSS>

<bs:linkScript>
    /js/bs/testConnection.js
</bs:linkScript>

<jsp:useBean id="settingsBean" class="com.quali.teamcity.plugins.qsSandbox.server.admin.QsSandboxBean"/>

<div id="settingsContainer">
  <form action="${actionUrl}" id="sandboxAdminForm" method="post" onsubmit="return SandboxAdmin.save()" >
    <div>
          <c:choose>
            <c:when test="${disabled}">
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
                        <forms:textField name="serverAddress" value="${serverAddress}" style="width: 300px;" />
                        <span class="smallNote">CloudShell Sandbox API address and port. By default, the Sandbox API is using port 82.
                                                For Example: http://192.168.1.1:82 or https://10.10.19.1:82</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="username">User Name: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="username" value="${username}" style="width: 300px;" />
                        <span class="smallNote">The CloudShell user name to use. This user will be used to authenticate through the Sandbox API.</span>
                    </td>
                </tr>

                <tr>
                    <th>
                        <label for="password">Password: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="password" value="${password}" style="width: 300px;" />
                        <span class="fieldExplanation">CloudShell password of the given user, In order to authenticate through the Sandbox API.</span>
                    </td>
                </tr>


                <tr>
                    <th>
                        <label for="domain">domain: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="domain" value="${domain}" style="width: 300px;" />
                        <span class="smallNote">CloudShell domain of the given user, In order to authenticate through the Sandbox API.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="ignoreSsl">Ignore SSL Certificate: <l:star /></label>
                    </th>
                    <td>
                        <forms:checkbox name="ignoreSsl" checked="${ignoreSsl}"/>
                        <span class="smallNote">If HTTPS communication is enabled in CloudShell Sandbox API without a singed certificate, check this option to ignore the certificate.</span>
                    </td>
                </tr>
           </table>

            <div class="saveButtonsBlock">
                <forms:submit label="Save" />
                <forms:submit id="testConnection" type="button" label="Test Connection" onclick="return SandboxAdmin.sendTestNotification()"/>
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

    <script type="text/javascript">
    var SandboxAdmin = {

        sendTestNotification : function() {

            jQuery.ajax(
                {
                    url: $("sandboxAdminForm").action,
                    data: {
                        test: 1,
                        serverAddress: $("serverAddress").value,
                        password: $("password").value,
                        username: $("username").value,
                        domain: $("domain").value,
                        ignoreSsl: $("ignoreSsl").value
                    },
                    type: "GET"
                }).done(function(data) {
                    var result = data.documentElement.innerHTML
                    var success = result.includes("Test completed successfully")
                    BS.TestConnectionDialog.show(success,result, $('testConnection'));
                });

            return false;
        },
        save : function() {

            jQuery.ajax(
                {
                    url: $("sandboxAdminForm").action,
                    data: {
                        edit: 1,
                        serverAddress: $("serverAddress").value,
                        password: $("password").value,
                        username: $("username").value,
                        domain: $("domain").value,
                        ignoreSsl: $("ignoreSsl").checked
                    },
                    type: "POST"
                }).done(function() {
                    BS.reload();
                }).fail(function(xhr, textStatus, errorThrown) {
                    alert("Failed to save configuration!")
                });

            return false;
        }
    }
    </script>
</div>



<script type="text/javascript">
	(function($) {
		var sendAction = function(enable) {
			$.post("${actionUrl}?action=" + (enable ? 'enable' : 'disable'),
					function() {
						BS.reload(true);
					});
			return false;
		};
		$("#enable-btn").click(function() {
			return sendAction(true);
		});
		$("#disable-btn")
            .click(
                function() {
                    if (!confirm("CloudShell Sandbox plugin will be suspended until enabled. Disable the plugin?"))
                        return false;
                    return sendAction(false);
                });
	})(jQuery);
</script>