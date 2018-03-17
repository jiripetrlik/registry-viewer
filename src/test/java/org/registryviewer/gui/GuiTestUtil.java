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

import org.registryviewer.connector.RegistryConnectionSettings;

public class GuiTestUtil {

    public static RegistryConnectionSettings createSimpleConnectionSettings() {
        RegistryConnectionSettings registryConnectionSettings = new RegistryConnectionSettings();
        registryConnectionSettings.setUrl("http://localhost:5000");
        registryConnectionSettings.setInsecure(false);
        registryConnectionSettings.setUseAuthentication(true);
        registryConnectionSettings.setUsername("user");
        registryConnectionSettings.setPassword("passwd");

        return registryConnectionSettings;
    }
}
