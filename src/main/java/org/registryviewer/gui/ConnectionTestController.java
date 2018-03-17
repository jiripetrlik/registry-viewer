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

package org.registryviewer.gui;

import org.registryviewer.service.ConnectorService;
import org.registryviewer.service.KnownExceptionMessagesTranslatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.annotation.SessionScope;

@Controller
@RequestMapping("test")
public class ConnectionTestController {

    private static final String CONTROLLER_URL = "/test";
    private static final String TEMPLATE_FOLDER = "test";

    private static final Logger logger = LoggerFactory.getLogger(ConnectionTestController.class);

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private KnownExceptionMessagesTranslatorService messagesTranslatorService;

    @RequestMapping(method = RequestMethod.GET)
    private String testConnection(Model model) {
        if (!connectorService.isInitialized()) {
            logger.error("It is not possible to test connection, which is not initialized");
            model.addAttribute("error", "Connection is not initialized");
            return TEMPLATE_FOLDER + "/result";
        }

        try {
            connectorService.getRegistryConnector().touch();
            logger.info("Registry connection was successfully tested {}",
                    connectorService.getRegistryConnector().getRegistryConnectionSettings().getUrl());
        } catch (RuntimeException e) {
            logger.error("Error in connection to registry {}, {}",
                    connectorService.getRegistryConnector().getRegistryConnectionSettings().getUrl(), e);
            model.addAttribute("error", messagesTranslatorService.translate(e));
        }

        return TEMPLATE_FOLDER + "/result";
    }
}
