<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="bean" class="com.quali.teamcity.plugins.sandbox.server.admin.QsSandboxBean"/>

<%@ page import="com.quali.teamcity.plugins.sandbox.common.Constants" %>
<c:set var="UI_START_SANDBOX" value="<%=Constants.START_SANDBOX%>"/>
<c:set var="UI_STOP_SANDBOX" value="<%=Constants.STOP_SANDBOX%>"/>

<l:settingsGroup title="CloudShell Sandbox Configuration">
  <tr>
    <th>
      CloudShell Action:
    </th>

    <jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

    <td>
      <c:set var="modeSelected" value="${propertiesBean.properties[bean.qsSandboxAction]}"/>
      <props:selectProperty name="${bean.qsSandboxAction}" onchange="BS.SandboxRunner.onModeChanged()" enableFilter="true" className="mediumField">
        <props:option value="${UI_START_SANDBOX}" currValue="${modeSelected}">Start Sandbox</props:option>
        <props:option value="${UI_STOP_SANDBOX}" currValue="${modeSelected}">Stop Sandbox</props:option>
      </props:selectProperty>
    </td>
  </tr>

  <tr id="sandbox.start.blueprint.container">
    <th><label for="${bean.qsSandboxBlueprintNameKey}">Blueprint: <l:star/></label></th>
    <td>
      <div class="completionIconWrapper">
        <props:textProperty name="${bean.qsSandboxBlueprintNameKey}" className="longField"/>
      </div>
      <span class="error" id="error_${bean.qsSandboxBlueprintNameKey}"></span>
      <span class="smallNote">Specify the Blueprint name in CloudShell</span>
    </td>
  </tr>

  <tr id="sandbox.start.duration.container">
    <th><label for="${bean.qsSandboxDurationKey}">Duration: <l:star/></label></th>
    <td>
      <div class="completionIconWrapper">
        <props:textProperty name="${bean.qsSandboxDurationKey}" className="longField"/>
      </div>
      <span class="error" id="error_${bean.qsSandboxDurationKey}"></span>
      <span class="smallNote">Specify the Sandbox duration in minutes</span>
    </td>
  </tr>

  <tr id="sandbox.start.timeout.container">
    <th><label for="${bean.qsSandboxTimeoutDurationKey}">Sandbox Timeout Duration: <l:star/></label></th>
    <td>
      <div class="completionIconWrapper">
        <props:textProperty name="${bean.qsSandboxTimeoutDurationKey}" className="longField"/>
      </div>
      <span class="error" id="error_${bean.qsSandboxTimeoutDurationKey}"></span>
      <span class="smallNote">Specify the Sandbox timeout duration in minutes</span>
    </td>
  </tr>

  <tr id="sandbox.start.params.container">
    <th><label for="${bean.qsSandboxParamKey}">Params: <l:star/></label></th>
    <td>
      <div class="completionIconWrapper">
        <props:textProperty name="${bean.qsSandboxParamKey}" className="longField"/>
      </div>
      <span class="error" id="error_${bean.qsSandboxParamKey}"></span>
      <span class="smallNote">Specify the Sandbox parameters (input1=aa;input2=ss)</span>
    </td>
  </tr>

    <tr id="sandbox.stop.id.container" style="display: none">
      <th><label for="${bean.qsSandboxId}">Sandbox Id: <l:star/></label></th>
      <td>
        <div class="completionIconWrapper">
          <props:textProperty name="${bean.qsSandboxId}" className="longField"/>
        </div>
        <span class="error" id="error_${bean.qsSandboxId}"></span>
        <span class="smallNote">Specify Sandbox Id to stop. by default, use %env.SANDBOX_ID% to stop the current running Sandbox in this build configuration</span>
      </td>
    </tr>
</l:settingsGroup>

<script type="text/javascript">
  BS.SandboxRunner = {
    onModeChanged:function () {
      var sel = $('${bean.qsSandboxAction}');
      var selectedValue = sel[sel.selectedIndex].value;
      if ('${UI_START_SANDBOX}' == selectedValue) {
        BS.Util.show('sandbox.start.blueprint.container');
        BS.Util.show('sandbox.start.duration.container');
        BS.Util.show('sandbox.start.timeout.container');
        BS.Util.show('sandbox.start.params.container');
        BS.Util.hide('sandbox.stop.id.container');

      } else if ('${UI_STOP_SANDBOX}' == selectedValue) {
        BS.Util.hide('sandbox.start.blueprint.container');
        BS.Util.hide('sandbox.start.duration.container');
        BS.Util.hide('sandbox.start.timeout.container');
        BS.Util.hide('sandbox.start.params.container');
        BS.Util.show('sandbox.stop.id.container');
      }

      BS.VisibilityHandlers.updateVisibility('mainContent');

    }
  };
  BS.SandboxRunner.onModeChanged();
</script>
