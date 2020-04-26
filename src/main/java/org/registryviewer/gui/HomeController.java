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

import org.registryviewer.RegistryConfigurationProperties;
import org.registryviewer.connector.RegistryConnectionSettings;
import org.registryviewer.service.ConnectorService;
import org.registryviewer.service.KnownExceptionMessagesTranslatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class HomeController {
    private static final String CONTROLLER_URL = "/";
    private static final String TEMPLATE_FOLDER = "home";

    @Autowired
    RegistryConfigurationProperties registryConfigurationProperties;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private KnownExceptionMessagesTranslatorService messagesTranslatorService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        if (connectorService.isInitialized()) {
            try {
                connectorService.getRegistryConnector().touch();
            } catch (RuntimeException e) {
                logger.error("Error testing registry connection {}", e);
                model.addAttribute("error", messagesTranslatorService.translate(e));
            }

            logger.debug("Page with information about connection was displayed");
            model.addAttribute("info", connectorService.getRegistryConnector());

            return TEMPLATE_FOLDER + "/info";
        } else {
            if (notNullOrEmpty(registryConfigurationProperties.getUrl())) {
                RegistryConnectionSettings settings = loadConnectionSettingsFromParams();
                connectorService.init(settings);
                logger.info("Connection to {} was initialized using configuration properties", settings.getUrl());

                return "redirect:" + "/";
            } else {
                logger.debug("Connection prompt was displayed");
                model.addAttribute("settings", new RegistryConnectionSettings());
                model.addAttribute("hideMenu", true);

                return TEMPLATE_FOLDER + "/connect";
            }
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String connect(@Valid @ModelAttribute("settings") RegistryConnectionSettings settings, BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hideMenu", true);
            logger.info("Invalid values of connection settings were given");
            return TEMPLATE_FOLDER + "/connect";
        }

        if (connectorService.isInitialized()) {
            connectorService.getRegistryConnector().disconnect();
            logger.info("Connector service was disconnected");
        }

        logger.info("Connecting to Docker registry: {}", settings.getUrl());
        connectorService.init(settings);

        return "redirect:" + "/";
    }

    @RequestMapping(value = "/disconnect", method = RequestMethod.GET)
    public String disconnect() {
        logger.info("Connection was closed");
        connectorService.disconnect();

        return "redirect:" + "/";
    }

    private RegistryConnectionSettings loadConnectionSettingsFromParams() {
        RegistryConnectionSettings registryConnectionSettings = new RegistryConnectionSettings();

        registryConnectionSettings.setUrl(registryConfigurationProperties.getUrl());
        registryConnectionSettings.setInsecure(registryConfigurationProperties.isInsecure());
        if (notNullOrEmpty(registryConfigurationProperties.getUsername())) {
            if (registryConfigurationProperties.getPassword() == null) {
                throw new RuntimeException("Registry password (registry.password) parameter is not set");
            }
            registryConnectionSettings.setUseAuthentication(true);
            registryConnectionSettings.setUsername(registryConfigurationProperties.getUsername());
            registryConnectionSettings.setPassword(registryConfigurationProperties.getPassword());
        } else {
            registryConnectionSettings.setUseAuthentication(false);
        }

        logger.info("Connection settings was loaded from configuration properties. Url={}",
                registryConnectionSettings.getUrl());
        return registryConnectionSettings;
    }

    private boolean notNullOrEmpty(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
