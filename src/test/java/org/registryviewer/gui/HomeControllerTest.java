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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.registryviewer.RegistryViewer;
import org.registryviewer.connector.RegistryConnectionSettings;
import org.registryviewer.connector.RegistryConnector;
import org.registryviewer.service.ConnectorService;
import org.registryviewer.service.KnownExceptionMessagesTranslatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RegistryViewer.class)
public class HomeControllerTest {

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
    public void testDisplayLoginScreen() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("//form[@id='connect_form']").exists());
        Mockito.verify(connectorService, Mockito.atLeast(1)).isInitialized();
    }

    @Test
    public void testLogin() throws Exception {
        ArgumentCaptor<RegistryConnectionSettings> connectionSettingsCaptor =
                ArgumentCaptor.forClass(RegistryConnectionSettings.class);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/")
                    .param("url", "http://localhost:5000")
                    .param("insecure", "false")
                    .param("useAuthentication", "true")
                    .param("username", "user")
                    .param("password", "passwd"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        Mockito.verify(connectorService).init(connectionSettingsCaptor.capture());
        RegistryConnectionSettings registryConnectionSettings = connectionSettingsCaptor.getValue();
        Assertions.assertThat(registryConnectionSettings).isNotNull();
        Assertions.assertThat(registryConnectionSettings.getUrl()).isEqualTo("http://localhost:5000");
        Assertions.assertThat(registryConnectionSettings.isInsecure()).isFalse();
        Assertions.assertThat(registryConnectionSettings.isUseAuthentication()).isTrue();
        Assertions.assertThat(registryConnectionSettings.getUsername()).isEqualTo("user");
        Assertions.assertThat(registryConnectionSettings.getPassword()).isEqualTo("passwd");

        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.when(registryConnector.getRegistryConnectionSettings()).thenReturn(registryConnectionSettings);
        Mockito.reset(connectorService);
        Mockito.when(connectorService.isInitialized()).thenReturn(true);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("//table[@class='info_table']").exists())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assertions.assertThat(content).contains("http://localhost:5000");

        Mockito.verify(registryConnector, Mockito.atLeast(1)).touch();
    }

    @Test
    public void testLoginError() throws Exception {
        RegistryConnectionSettings registryConnectionSettings = GuiTestUtil.createSimpleConnectionSettings();
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.when(registryConnector.getRegistryConnectionSettings()).thenReturn(registryConnectionSettings);
        Mockito.doThrow(new RuntimeException("Connection error")).when(registryConnector).touch();
        Mockito.when(connectorService.isInitialized()).thenReturn(true);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        Assertions.assertThat(content).contains("displayConnectionErrorDialog(error);");
        Assertions.assertThat(content).contains("Connection error");
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/disconnect"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
        Mockito.verify(connectorService, Mockito.atLeast(1)).disconnect();
    }
}
