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

public class ConnectorConstants {

    public static final String V2_API_SUFFIX = "v2";
    public static final String CATALOG_SUFFIX = "_catalog";
    public static final String TAGS_SUFFIX = "tags";
    public static final String LIST_SUFFIX = "list";
    public static final String MANIFESTS_SUFFIX = "manifests";

    public static final String N_QUERY_PARAM = "n";
    public static final String LAST_QUERY_PARAM = "last";

    public static final String DOCKER_CONTENT_DIGEST_HEADER = "Docker-Content-Digest";
    public static final String DELETE_TAG_ACCEPT_HEADER = "application/vnd.docker.distribution.manifest.v2+json";
}
