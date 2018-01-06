package org.registryviewer.gui;

import org.registryviewer.service.ConnectorService;
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

    @Autowired
    private ConnectorService connectorService;

    @RequestMapping(method = RequestMethod.GET)
    private String testConnection(Model model) {
        if (!connectorService.isInitialized()) {
            model.addAttribute("error", "Connection is not initialized");
            return TEMPLATE_FOLDER + "/result";
        }

        try {
            connectorService.getRegistryConnector().touch();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        return TEMPLATE_FOLDER + "/result";
    }
}
