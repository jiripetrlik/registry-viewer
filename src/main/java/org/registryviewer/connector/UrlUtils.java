package org.registryviewer.connector;

import org.springframework.web.util.UriComponentsBuilder;

public class UrlUtils {

    public static final UriComponentsBuilder catalogUrl(String registryUrl) {
        UriComponentsBuilder builder = baseUriBuilder(registryUrl);
        builder = builder.pathSegment(ConnectorConstants.CATALOG_SUFFIX);

        return builder;
    }

    public static final UriComponentsBuilder tagsUrl(String registryUrl, String repository) {
        UriComponentsBuilder builder = baseUriBuilder(registryUrl);
        builder = builder.pathSegment(repository);
        builder = builder.pathSegment(ConnectorConstants.TAGS_SUFFIX);
        builder = builder.pathSegment(ConnectorConstants.LIST_SUFFIX);

        return builder;
    }

    public static final UriComponentsBuilder manifestUrl(String registryUrl, String repository, String tag) {
        UriComponentsBuilder builder = baseUriBuilder(registryUrl);
        builder = builder.pathSegment(repository);
        builder = builder.pathSegment(ConnectorConstants.MANIFESTS_SUFFIX);
        builder = builder.pathSegment(tag);

        return builder;
    }

    public static final UriComponentsBuilder deleteTagUrl(String registryUrl, String repository, String hash) {
        UriComponentsBuilder builder = baseUriBuilder(registryUrl);
        builder = builder.pathSegment(repository);
        builder = builder.pathSegment(ConnectorConstants.MANIFESTS_SUFFIX);
        builder = builder.pathSegment(hash);

        return builder;
    }

    private static final UriComponentsBuilder baseUriBuilder(String registryUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(registryUrl);
        builder = builder.pathSegment(ConnectorConstants.V2_API_SUFFIX);

        return builder;
    }
}
