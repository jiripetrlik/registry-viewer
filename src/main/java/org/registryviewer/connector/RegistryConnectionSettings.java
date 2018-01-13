package org.registryviewer.connector;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegistryConnectionSettings {

    @NotNull
    @Size(min = 1)
    private String url;

    @NotNull
    private boolean insecure;

    @NotNull
    private boolean useAuthentication;

    @Size(min = 1)
    private String username;

    @Size(min = 1)
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isInsecure() {
        return insecure;
    }

    public void setInsecure(boolean insecure) {
        this.insecure = insecure;
    }

    public boolean isUseAuthentication() {
        return useAuthentication;
    }

    public void setUseAuthentication(boolean useAuthentication) {
        this.useAuthentication = useAuthentication;
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
}
