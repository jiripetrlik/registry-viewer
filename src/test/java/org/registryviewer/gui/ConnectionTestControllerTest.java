package org.registryviewer.gui;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.registryviewer.RegistryViewer;
import org.registryviewer.connector.RegistryConnector;
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

import java.io.UnsupportedEncodingException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RegistryViewer.class)
public class ConnectionTestControllerTest {

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
    public void testConnectionNotInitializedTest() throws Exception {
        Mockito.when(connectorService.isInitialized()).thenReturn(false);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assertions.assertThat(content).contains("Connection error: Connection is not initialized");
    }

    @Test
    public void testConnectionOk() throws Exception {
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.when(registryConnector.getRegistryConnectionSettings()).thenReturn(GuiTestUtil.createSimpleConnectionSettings());
        Mockito.when(connectorService.isInitialized()).thenReturn(true);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assertions.assertThat(content).contains("Successfully connected!");
    }

    @Test
    public void testConnectionError() throws Exception {
        RegistryConnector registryConnector = Mockito.mock(RegistryConnector.class);
        Mockito.doThrow(new RuntimeException("Connection error")).when(registryConnector).touch();
        Mockito.when(registryConnector.getRegistryConnectionSettings()).thenReturn(GuiTestUtil.createSimpleConnectionSettings());
        Mockito.when(connectorService.isInitialized()).thenReturn(true);
        Mockito.when(connectorService.getRegistryConnector()).thenReturn(registryConnector);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assertions.assertThat(content).contains("Connection error");
    }
}
