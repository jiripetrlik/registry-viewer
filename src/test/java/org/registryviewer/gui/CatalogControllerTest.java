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

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.registryviewer.RegistryViewer;
import org.registryviewer.connector.RegistryConnector;
import org.registryviewer.connector.model.Manifest;
import org.registryviewer.connector.model.ManifestConfig;
import org.registryviewer.connector.model.Repositories;
import org.registryviewer.connector.model.Tags;
import org.registryviewer.service.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RegistryViewer.class)
public class CatalogControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private ConnectorService connectorService;

    @Before
    public void setup() {
        Mockito.reset(connectorService);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testCatalogList() throws Exception {
        Repositories repositories = new Repositories();
        repositories.setRepositories(Arrays.asList("repo1", "repo2"));
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.when(registryConnector.listRepositories(20)).thenReturn(repositories);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/catalog/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("//table[@class='info_table']").exists())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        Assertions.assertThat(content).contains("repo1");
        Assertions.assertThat(content).contains("repo2");

        Mockito.verify(registryConnector, Mockito.atLeast(1)).listRepositories(20);
    }

    @Test
    public void testCatalogListWithLast() throws Exception {
        Repositories repositories = new Repositories();
        repositories.setRepositories(Arrays.asList("repo1", "repo2"));
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.when(registryConnector.listRepositories(20, "repoLast")).thenReturn(repositories);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/catalog/list/repoLast"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("//table[@class='info_table']").exists())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        Assertions.assertThat(content).contains("repo1");
        Assertions.assertThat(content).contains("repo2");

        Mockito.verify(registryConnector, Mockito.atLeast(1)).listRepositories(20, "repoLast");
    }

    @Test
    public void testListTags() throws Exception {
        Tags tags = new Tags();
        tags.setName("repo");
        tags.setTags(Arrays.asList("tag1", "tag2"));
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.when(registryConnector.listTags("repo")).thenReturn(tags);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/catalog/tags/repo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("//table[@class='info_table']").exists())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        Assertions.assertThat(content).contains("tag1");
        Assertions.assertThat(content).contains("tag2");

        Mockito.verify(registryConnector, Mockito.atLeast(1)).listTags("repo");
    }

    @Test
    public void testDeleteTag() throws Exception {
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);

        mockMvc.perform(MockMvcRequestBuilders.get("/catalog/tags/delete/repo1/tag1"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        Mockito.verify(registryConnector, Mockito.atLeast(1)).deleteTag("repo1", "tag1");
    }

    @Test
    public void testManifestDetails() throws Exception {
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        ManifestConfig manifestConfig = new ManifestConfig();
        manifestConfig.setDigest("");
        manifestConfig.setMediaType("");
        Manifest manifest = new Manifest();
        manifest.setConfig(manifestConfig);
        manifest.setMediaType("");
        manifest.setLayers(new ArrayList<>());
        Mockito.when(registryConnector.manifest("repo1", "tag1")).thenReturn(manifest);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);

        mockMvc.perform(MockMvcRequestBuilders.get("/catalog/manifest/repo1/tag1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("//table[@class='info_table']").exists())
                .andReturn();

        Mockito.verify(registryConnector, Mockito.atLeast(1)).manifest("repo1", "tag1");
    }
}
