package org.registryviewer.gui;

import org.registryviewer.connector.model.Manifest;
import org.registryviewer.connector.model.Repositories;
import org.registryviewer.connector.model.Tags;
import org.registryviewer.service.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.stream.Collectors;

@Controller
@RequestMapping("catalog")
public class CatalogController {

    private static final int ITEMS_PER_PAGE = 5;
    private static final String CONTROLLER_URL = "/catalog";
    private static final String TEMPLATE_FOLDER = "catalog";

    @Autowired
    private ConnectorService connectorService;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        try {
            Repositories repositories = connectorService.getRegistryConnector().listRepositories(ITEMS_PER_PAGE);
            model.addAttribute("repositories", repositories);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return TEMPLATE_FOLDER + "/list";
    }

    @RequestMapping(value = "list/{last}")
    public String list(@PathVariable String last, Model model) {
        try {
            Repositories repositories = connectorService.getRegistryConnector().listRepositories(ITEMS_PER_PAGE, last);
            model.addAttribute("repositories", repositories);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return TEMPLATE_FOLDER + "/list";
    }

    @RequestMapping(value = "tags/{repository}")
    public String tags(@PathVariable("repository") String repository, Model model) {
        try {
            Tags tags = connectorService.getRegistryConnector().listTags(repository);
            tags.setTags(tags.getTags().stream().sorted().collect(Collectors.toList()));
            model.addAttribute("tags", tags);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return TEMPLATE_FOLDER + "/tags";
    }

    @RequestMapping(value = "tags/delete/{repository}/{tag}")
    public String deleteTag(@PathVariable("repository") String repository, @PathVariable("tag") String tag,
                            Model model) {
        connectorService.getRegistryConnector().deleteTag(repository, tag);

        return "redirect:/";
    }

    @RequestMapping(value = "manifest/{repository}/{tag}")
    public String manifestDetails(@PathVariable("repository") String repository, @PathVariable("tag") String tag, Model model) {
        try {
            Manifest manifest = connectorService.getRegistryConnector().manifest(repository, tag);
            model.addAttribute("manifest", manifest);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return TEMPLATE_FOLDER + "/detail";
    }
}
