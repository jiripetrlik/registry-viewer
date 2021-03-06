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

package org.registryviewer.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.registryviewer.RegistryViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RegistryViewer.class)
public class KnownExceptionMessagesTranslatorServiceTest {

    @Autowired
    KnownExceptionMessagesTranslatorService knownExceptionMessagesTranslatorService;

    @Test
    public void testInitialization() {
        Assertions.assertThat(knownExceptionMessagesTranslatorService).isNotNull();
        Assertions.assertThat(knownExceptionMessagesTranslatorService.getTranslationItems().size()).isGreaterThan(0);
    }

    @Test
    public void testNonTranslateable() {
        final String NON_TRANSLATEABLE_TEXT = "Non translatable text";
        Exception exception = new RuntimeException(NON_TRANSLATEABLE_TEXT);
        String text = knownExceptionMessagesTranslatorService.translate(exception);
        Assertions.assertThat(text).isEqualTo("java.lang.RuntimeException: " + NON_TRANSLATEABLE_TEXT);
    }

    @Test
    public void testSimpleTranslation() {
        Exception exception = new RuntimeException("Connection refused");
        String text = knownExceptionMessagesTranslatorService.translate(exception);
        Assertions.assertThat(text).isEqualTo("Connection refused by remote host");
    }

    @Test
    public void testInnerExceptionTranslation() {
        Exception innerException = new RuntimeException("Connection refused");
        Exception outerException = new RuntimeException("Outer exception", innerException);
        String text = knownExceptionMessagesTranslatorService.translate(outerException);
        Assertions.assertThat(text).isEqualTo("Connection refused by remote host");
    }
}
