package org.registryviewer.gui;

import org.registryviewer.connector.RegistryConnectionSettings;
import org.registryviewer.service.ConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ConnectorService connectorService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        if (connectorService.isInitialized()) {
            try {
                connectorService.getRegistryConnector().touch();
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
            }

            logger.info("Page with information about connection was displayed");
            model.addAttribute("info", connectorService.getRegistryConnector());
            return TEMPLATE_FOLDER + "/info";
        } else {
            logger.info("Connection prompt was displayed");
            model.addAttribute("settings", new RegistryConnectionSettings());
            model.addAttribute("hideMenu", true);
            return TEMPLATE_FOLDER + "/connect";
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String connect(@Valid @ModelAttribute("settings") RegistryConnectionSettings settings, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
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
        connectorService.disconnect();

        return "redirect:" + "/";
    }
}
