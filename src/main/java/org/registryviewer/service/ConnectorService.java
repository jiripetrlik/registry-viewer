/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public void setRegistryConnector(RegistryConnector registryConnector) {
        this.registryConnector = Optional.of(registryConnector);
    }

    public synchronized void disconnect() {
        if (registryConnector.isPresent()) {
            registryConnector.get().disconnect();
            registryConnector = Optional.empty();
        }
    }
}
