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

import org.registryviewer.connector.model.Manifest;
import org.registryviewer.connector.model.Repositories;
import org.registryviewer.connector.model.Tags;
import org.registryviewer.service.ConnectorService;
import org.registryviewer.service.KnownExceptionMessagesTranslatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final int ITEMS_PER_PAGE = 20;
    private static final String CONTROLLER_URL = "/catalog";
    private static final String TEMPLATE_FOLDER = "catalog";

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private KnownExceptionMessagesTranslatorService messagesTranslatorService;

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        try {
            Repositories repositories = connectorService.getRegistryConnector().listRepositories(ITEMS_PER_PAGE);
            model.addAttribute("repositories", repositories);
        } catch (Exception e) {
            logger.error("Error loading list of repositories: {}", e);
            model.addAttribute("error", messagesTranslatorService.translate(e));
        }

        logger.debug("List of repositories was displayed");
        return TEMPLATE_FOLDER + "/list";
    }

    @RequestMapping(value = "list/{last}")
    public String list(@PathVariable String last, Model model) {
        try {
            Repositories repositories = connectorService.getRegistryConnector().listRepositories(ITEMS_PER_PAGE, last);
            model.addAttribute("repositories", repositories);
        } catch (Exception e) {
            logger.error("Error loading list of repositories: {}", e);
            model.addAttribute("error", messagesTranslatorService.translate(e));
        }

        logger.debug("List of repositories was displayed");
        return TEMPLATE_FOLDER + "/list";
    }

    @RequestMapping(value = "tags/{repository}")
    public String tags(@PathVariable("repository") String repository, Model model) {
        try {
            Tags tags = connectorService.getRegistryConnector().listTags(repository);
            if (tags != null && tags.getTags() !=null) {
                tags.setTags(tags.getTags().stream().sorted().collect(Collectors.toList()));
            }
            model.addAttribute("tags", tags);
        } catch (Exception e) {
            logger.error("Error loading tags for repository {}, exception: {}", repository, e);
            model.addAttribute("error", messagesTranslatorService.translate(e));
        }

        logger.debug("List of tags for repository {} was displayed", repository);
        return TEMPLATE_FOLDER + "/tags";
    }

    @RequestMapping(value = "tags/delete/{repository}/{tag}")
    public String deleteTag(@PathVariable("repository") String repository, @PathVariable("tag") String tag,
                            Model model) {
        connectorService.getRegistryConnector().deleteTag(repository, tag);

        logger.debug("Tag {} from repository {} was deleted", tag, repository);
        return "redirect:/";
    }

    @RequestMapping(value = "manifest/{repository}/{tag}")
    public String manifestDetails(@PathVariable("repository") String repository, @PathVariable("tag") String tag, Model model) {
        try {
            Manifest manifest = connectorService.getRegistryConnector().manifest(repository, tag);
            model.addAttribute("manifest", manifest);
        } catch (Exception e) {
            logger.error("Error loading manifest for tag {} in repository {}", tag, repository);
            model.addAttribute("error", messagesTranslatorService.translate(e));
        }

        logger.debug("Manifest was displayed for tag {} in repository {}", tag, repository);
        return TEMPLATE_FOLDER + "/detail";
    }
}
