package com.quali.teamcity.plugins.sandbox.server;

import com.quali.teamcity.plugins.sandbox.common.Constants;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.crypt.EncryptUtil;
import jetbrains.buildServer.util.StringUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class SandboxAdminSettings {
    public ServerPaths serverPaths;
    private String serverAddress = "";
    private String username = "";
    private String password = "";
    private String domain = "";
    private boolean ignoreSsl = false;
    private boolean disabled = true;

    public SandboxAdminSettings(ServerPaths serverPaths) {
        this.serverPaths = serverPaths;
    }

    public void init() {
        loadProperties();
    }

    public void saveProperties() throws IOException {
        File keyFile = propFile();
        if (keyFile == null) {
            throw new RuntimeException("Property file not found");
        }

        Properties prop = new Properties();
        FileWriter outFile = new FileWriter(keyFile);
        prop.put("serverAddress", this.serverAddress);
        prop.put("username", this.username);
        prop.put("password", scramble(this.password));
        prop.put("domain", this.domain);
        prop.put("ignoreSsl", Boolean.toString(this.ignoreSsl));
        prop.put("disabled", Boolean.toString(this.disabled));
        prop.store(outFile, null);
        outFile.close();
    }

    public void loadProperties() {
        File keyFile = propFile();
        if (keyFile == null) {
            return;
        }

        FileReader inFile = null;
        try {
            inFile = new FileReader(keyFile);
            Properties prop = new Properties();
            prop.load(inFile);

            this.serverAddress = prop.getProperty("serverAddress", "");
            this.username = prop.getProperty("username", "");
            this.password = unscramble(prop.getProperty("password", ""));
            this.domain = prop.getProperty("domain", "");
            this.disabled = Boolean.parseBoolean(prop.getProperty("disabled", "false"));

            inFile.close();
        } catch (IOException e) {
        } finally {
            try {
                if (inFile != null) {
                    inFile.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private File propFile() {
        File keyFile = new File(serverPaths.getConfigDir() + Constants.CS_PROPERTIES_FILE);
        if (!keyFile.exists()) {
            try {
                boolean created = keyFile.createNewFile();
                if (!created) {
                }
            } catch (IOException e) {
                return null;
            }
        }
        return keyFile;
    }

    private String scramble(String str) {
        return StringUtil.isEmpty(str) ? str : EncryptUtil.scramble(str);
    }

    private String unscramble(String str) {
        return StringUtil.isEmpty(str) ? str : EncryptUtil.unscramble(str);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean getIgnoreSsl() {
        return ignoreSsl;
    }

    public void setIgnoreSsl(boolean ignoreSsl) {
        this.ignoreSsl = ignoreSsl;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean enabled) {
        this.disabled = enabled;
    }
}
