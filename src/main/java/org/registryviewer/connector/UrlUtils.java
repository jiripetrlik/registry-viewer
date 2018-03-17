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
