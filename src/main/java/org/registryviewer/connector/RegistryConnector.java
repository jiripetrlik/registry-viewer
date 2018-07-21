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

package org.registryviewer.connector;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.registryviewer.connector.model.Manifest;
import org.registryviewer.connector.model.Repositories;
import org.registryviewer.connector.model.Tags;
import org.registryviewer.gui.AppErrorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

public class RegistryConnector {

    private RestTemplate restTemplate;
    private RegistryConnectionSettings registryConnectionSettings;

    private static final Logger logger = LoggerFactory.getLogger(RegistryConnector.class);

    public RegistryConnector(RegistryConnectionSettings registryConnectionSettings) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

        if (registryConnectionSettings.isInsecure()) {
            restTemplateBuilder = allowInsecureRegistry(restTemplateBuilder);
        }

        if (registryConnectionSettings.isUseAuthentication()) {
            restTemplateBuilder = restTemplateBuilder.basicAuthorization(registryConnectionSettings.getUsername(),
                    registryConnectionSettings.getPassword());
        }

        this.restTemplate = restTemplateBuilder.build();
        this.registryConnectionSettings = registryConnectionSettings;

        logger.debug("Registry connector was created for url: {}", registryConnectionSettings.getUrl());
    }

    public RegistryConnectionSettings getRegistryConnectionSettings() {
        return registryConnectionSettings;
    }

    public void touch() {
        ResponseEntity<String> response = restTemplate.getForEntity(registryConnectionSettings.getUrl(), String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Unable to touch registry {}, http code {}, response={}", registryConnectionSettings.getUrl(),
                    response.getStatusCode(), response.toString());
            throw new RuntimeException(String.format("Unable to connect to %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }
        logger.debug("Docker registry were successfully touched: {}", registryConnectionSettings.getUrl());
    }

    public Repositories listRepositories(int n) {
        return listRepositories(n, Optional.empty());
    }

    public Repositories listRepositories(int n, String last) {
        return listRepositories(n, Optional.of(last));
    }

    private Repositories listRepositories(int n, Optional<String> last) {
        UriComponentsBuilder uriComponentsBuilder = UrlUtils.catalogUrl(registryConnectionSettings.getUrl());
        uriComponentsBuilder.queryParam(ConnectorConstants.N_QUERY_PARAM, n);
        if (last.isPresent()) {
            uriComponentsBuilder.queryParam(ConnectorConstants.LAST_QUERY_PARAM, last.get());
        }

        ResponseEntity<Repositories> response = restTemplate.getForEntity(uriComponentsBuilder.build().encode().toUri(), Repositories.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Unable to get repository list from {}. Status code: {}. Error message: {}",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString());
            throw new RuntimeException(String.format("Unable get repository list from %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }

        logger.debug("List of repositories was obtained from server {}", registryConnectionSettings.getUrl());
        return response.getBody();
    }

    public Tags listTags(String repository) {
        UriComponentsBuilder uriComponentsBuilder = UrlUtils.tagsUrl(registryConnectionSettings.getUrl(), repository);
        ResponseEntity<Tags> response = restTemplate.getForEntity(uriComponentsBuilder.build().encode().toUri(), Tags.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Unable to list tags for repository {} from {}. Status code: {}. Error message: {}",
                    registryConnectionSettings.getUrl(), repository, response.getStatusCodeValue(), response.toString());
            throw new RuntimeException(String.format("Unable to list tags from %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }

        logger.debug("List of tags for repository: {} was obtained from {}",
                repository, registryConnectionSettings.getUrl());
        return response.getBody();
    }

    public Manifest manifest(String repository, String tag) {
        UriComponentsBuilder uriComponentsBuilder = UrlUtils.manifestUrl(registryConnectionSettings.getUrl(),
                repository, tag);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", ConnectorConstants.DELETE_TAG_ACCEPT_HEADER);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Manifest> response = restTemplate.exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.GET, httpEntity, Manifest.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Unable to get manifest for repository {}, tag {} from {}. Error code {}, message {}",
                    repository, tag, registryConnectionSettings.getUrl(), response.getStatusCodeValue(),
                    response.toString());
            throw new RuntimeException(String.format("Unable to get manifest for repository from %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }

        logger.debug("Manifest for repository {} tag {} was obtained from {}", repository, tag, registryConnectionSettings.getUrl());
        return response.getBody();
    }

    public String digestHash(String repository, String tag) {
        UriComponentsBuilder uriComponentsBuilder = UrlUtils.manifestUrl(registryConnectionSettings.getUrl(),
                repository, tag);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", ConnectorConstants.DELETE_TAG_ACCEPT_HEADER);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.GET, httpEntity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Unable to get digest hash for repository {}, tag {} from {}. Error code {}, message {}",
                    repository, tag, registryConnectionSettings.getUrl(), response.getStatusCodeValue(),
                    response.toString());
            throw new RuntimeException(String.format("Unable to get digest hash for repository from %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }
        List<String> digestHeader = response.getHeaders().get(ConnectorConstants.DOCKER_CONTENT_DIGEST_HEADER);

        logger.debug("Digest hash for repository {} tag {} was obtained from {}", repository, tag, registryConnectionSettings.getUrl());
        return digestHeader.get(0);
    }

    public void deleteTag(String repository, String tag) {
        String hash = digestHash(repository, tag);
        UriComponentsBuilder uriComponentsBuilder = UrlUtils.deleteTagUrl(registryConnectionSettings.getUrl(),
                repository, hash);
        URI request = uriComponentsBuilder.build().encode().toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", ConnectorConstants.DELETE_TAG_ACCEPT_HEADER);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(request, HttpMethod.DELETE, httpEntity, String.class);
        logger.debug("Tag {} from repository {} was deleted. Registry url: {}",
                tag, repository, registryConnectionSettings.getUrl());
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void disconnect() {
        logger.debug("Disconnected from registry: {}", registryConnectionSettings.getUrl());
    }

    private static RestTemplateBuilder allowInsecureRegistry(RestTemplateBuilder restTemplateBuilder) {
        X509TrustManager tm = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize SSL context", e);
        }

        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
        ssf.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(ssf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        logger.debug("Rest template, which allows insecure connections, was created");
        return restTemplateBuilder.requestFactory(() -> requestFactory);
    }
}
