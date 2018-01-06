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
    }

    public RegistryConnectionSettings getRegistryConnectionSettings() {
        return registryConnectionSettings;
    }

    public void touch() {
        ResponseEntity<String> response = restTemplate.getForEntity(registryConnectionSettings.getUrl(), String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException(String.format("Unable to connect to %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }
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
            throw new RuntimeException(String.format("Unable to connect to %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }

        return response.getBody();
    }

    public Tags listTags(String repository) {
        UriComponentsBuilder uriComponentsBuilder = UrlUtils.tagsUrl(registryConnectionSettings.getUrl(), repository);
        ResponseEntity<Tags> response = restTemplate.getForEntity(uriComponentsBuilder.build().encode().toUri(), Tags.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException(String.format("Unable to connect to %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }

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
            throw new RuntimeException(String.format("Unable to connect to %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }
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
            throw new RuntimeException(String.format("Unable to connect to %s. Status code: %d. Error message: %s",
                    registryConnectionSettings.getUrl(), response.getStatusCodeValue(), response.toString()));
        }
        List<String> digestHeader = response.getHeaders().get(ConnectorConstants.DOCKER_CONTENT_DIGEST_HEADER);
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
        ;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void disconnect() {
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

        return restTemplateBuilder.requestFactory(requestFactory);
    }
}
