package org.registryviewer.service;

import org.registryviewer.connector.RegistryConnectionSettings;
import org.registryviewer.connector.RegistryConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;

@Service
@SessionScope
public class ConnectorService {

    private Optional<RegistryConnector> registryConnector = Optional.empty();

    public synchronized void init(RegistryConnectionSettings connectionSettings) {
        registryConnector = Optional.of(new RegistryConnector(connectionSettings));
    }

    public boolean isInitialized() {
        return registryConnector.isPresent();
    }

    public RegistryConnector getRegistryConnector() {
        return registryConnector.get();
    }

    public synchronized void disconnect() {
        if (registryConnector.isPresent()) {
            registryConnector.get().disconnect();
            registryConnector = Optional.empty();
        }
    }
}
