<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="buildForm" type="jetbrains.buildServer.controllers.admin.projects.BuildTypeForm" scope="request"/>
<jsp:useBean id="bean" class="com.quali.teamcity.plugins.qsSandbox.server.admin.QsSandboxBean"/>

<style type="text/css">
</style>

<tr>
  <td colspan="2"><em>This build feature allows running build configuration attached to a CloudShell Sandbox.</em></td>
</tr>

<tr>
  <th><label for="${bean.qsSandboxBlueprintNameKey}">Blueprint: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:textProperty name="${bean.qsSandboxBlueprintNameKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxBlueprintNameKey}"></span>
    <span class="smallNote">Specify the Blueprint name in CloudShell</span>
  </td>
</tr>

<tr>
  <th><label for="${bean.qsSandboxDurationKey}">Duration: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:textProperty name="${bean.qsSandboxDurationKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxDurationKey}"></span>
    <span class="smallNote">Specify the Sandbox duration in minutes</span>
  </td>
</tr>

<tr>
  <th><label for="${bean.qsSandboxTimeoutDurationKey}">Sandbox Timeout Duration: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:textProperty name="${bean.qsSandboxTimeoutDurationKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxTimeoutDurationKey}"></span>
    <span class="smallNote">Specify the Sandbox timeout duration in minutes</span>
  </td>
</tr>

<tr>
  <th><label for="${bean.qsSandboxParamKey}">Params: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:textProperty name="${bean.qsSandboxParamKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxParamKey}"></span>
    <span class="smallNote">Specify the Sandbox parameters (input1=aa;input2=ss)</span>
  </td>
</tr>


