package org.registryviewer.gui;

import org.registryviewer.connector.RegistryConnectionSettings;

public class GuiTestUtil {

    public static RegistryConnectionSettings createSimpleConnectionSettings() {
        RegistryConnectionSettings registryConnectionSettings = new RegistryConnectionSettings();
        registryConnectionSettings.setUrl("http://localhost:5000");
        registryConnectionSettings.setInsecure(false);
        registryConnectionSettings.setUseAuthentication(true);
        registryConnectionSettings.setUsername("user");
        registryConnectionSettings.setPassword("passwd");

        return registryConnectionSettings;
    }
}
