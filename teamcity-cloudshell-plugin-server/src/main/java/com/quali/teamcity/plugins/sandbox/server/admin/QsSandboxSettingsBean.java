package com.quali.teamcity.plugins.sandbox.server.admin;

import jetbrains.buildServer.controllers.RememberState;
import jetbrains.buildServer.controllers.StateField;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.StringUtil;

public class QsSandboxSettingsBean  extends RememberState {

  @StateField
  private String serverAddress;
  @StateField
  private String username;
  @StateField
  private String password;
  @StateField
  private String domain;
  @StateField
  private boolean ignoreSsl;
  @StateField
  private boolean disabled;


  public QsSandboxSettingsBean(String serverAddress, String username, String password, String domain, boolean ignoreSsl, boolean disabled) {
    this.serverAddress = serverAddress;
    this.username = username;
    this.password = password;
    this.domain = domain;
    this.ignoreSsl = ignoreSsl;
    this.disabled = disabled;
    rememberState();
  }

  public String getHexEncodedPublicKey() {
    return RSACipher.getHexEncodedPublicKey();
  }

  public boolean getIgnoreSsl() {
    return ignoreSsl;
  }

  public String getServerAddress() {
    return serverAddress;
  }

  public void setServerAddress(String serverAddress) {
    this.serverAddress = serverAddress;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEncryptedPassword() {
    return StringUtil.isEmpty(password) ? "" : RSACipher.encryptDataForWeb(password);
  }

  public void setEncryptedPassword(String password) {
    this.password = RSACipher.decryptWebRequestData(password);
  }

  public String getPassword() {
    return password;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public boolean isIgnoreSsl() {
    return ignoreSsl;
  }

  public void setIgnoreSsl(boolean ignoreSsl) {
    this.ignoreSsl = ignoreSsl;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
}

