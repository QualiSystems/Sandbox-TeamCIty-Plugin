<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="buildForm" type="jetbrains.buildServer.controllers.admin.projects.BuildTypeForm" scope="request"/>
<jsp:useBean id="bean" class="com.quali.teamcity.plugins.qsSandbox.server.QsSandboxBean"/>

<style type="text/css">
</style>

<tr>
  <td colspan="2"><em>This build feature allows running build configuration attached to a CloudShell Sandbox.</em></td>
</tr>

<tr>
  <th><label for="${bean.qsSandboxUserKey}">User name: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:textProperty name="${bean.qsSandboxUserKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxUserKey}"></span>
  </td>
</tr>

<tr>
  <th><label for="${bean.qsSandboxPasswordKey}">Password: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:passwordProperty name="${bean.qsSandboxPasswordKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxPasswordKey}"></span>
  </td>
</tr>

<tr>
  <th><label for="${bean.qsSandboxDomainKey}">Domain: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:textProperty name="${bean.qsSandboxDomainKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxDomainKey}"></span>
    <span class="smallNote">Specify the user CloudShell domain</span>
  </td>
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
  <th><label for="${bean.qsSandboxServerKey}">Server: <l:star/></label></th>
  <td>
    <div class="completionIconWrapper">
      <props:textProperty name="${bean.qsSandboxServerKey}" className="longField"/>
    </div>
    <span class="error" id="error_${bean.qsSandboxServerKey}"></span>
    <span class="smallNote">Specify the CloudShell server & port. example: http://10.10.10.10:82</span>
  </td>
</tr>



