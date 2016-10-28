/*-
 * #%L
 * Secured Properties
 * ===============================================================
 * Copyright (C) 2016 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.brabenetz.lib.security.properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import net.brabenetz.lib.security.properties.core.Encryption;
import net.brabenetz.lib.security.properties.test.TestUtils;
import net.brabenetz.lib.security.properties.utils.SecuredPropertiesUtils;

public class SecuredPropertiesTest {

    @Before
    public void before() throws Exception {
        System.clearProperty("mySecretPassword");
        if (getTestPropertyFile().exists()) {
            FileUtils.forceDelete(getTestPropertyFile());
        }
        if (getTestSecretFile().exists()) {
            FileUtils.forceDelete(getTestSecretFile());
        }

    }

    @Test
    public void testGetSecretValue_fromEncryptedPropertyFile() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword={wNnuFmepE9cAN6GpaULDZw==}");

        // run test
        String secretValue = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
            getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        assertThat(props.get("mySecretPassword"), is("{wNnuFmepE9cAN6GpaULDZw==}"));

    }

    @Test
    public void testGetSecretValue_fromUnencryptedPropertyFile_shouldReplaceProperty() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        String secretValue = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
            getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        String newPasswordValue = props.getProperty("mySecretPassword");
        assertThat(newPasswordValue, is(not("test")));
        assertThat(Encryption.isEncryptedPassword(newPasswordValue), is(true));

    }

    @Test
    public void testGetSecretValue_fromEncryptedSystemProperty() throws Exception {
        // prepare SystemProperty
        writeProperties(getTestPropertyFile(), "title=Some Test");
        System.setProperty("mySecretPassword", "{wNnuFmepE9cAN6GpaULDZw==}");

        // run test
        String secretValue = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
            getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is("test"));

    }

    @Test
    public void testGetSecretValue_notDefinedProperty_shouldReturnNull() throws Exception {
        // prepare SystemProperty
        writeProperties(getTestPropertyFile(), "title=Some Test");

        // run test
        String secretValue = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
            getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is(nullValue()));

    }

    @Test
    public void testGetSecretValue_fromUnencryptedSystemProperty_shouldLogInfoMessage() throws Exception {
        // prepare SystemProperty
        writeProperties(getTestPropertyFile(), "title=Some Test");
        System.setProperty("mySecretPassword", "test");

        // run test
        String secretValue = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
            getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is("test"));

        // TODO brabenetz 28.10.2016 : validate Log Message
    }

    @Test
    public void testGetSecretValue_fromUnencryptedPropertyFile_shouldNotReplaceProperty() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        String secretValue = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()).disableAutoEncryptNonEncryptedValues(),
            getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        String newPasswordValue = props.getProperty("mySecretPassword");
        assertThat(newPasswordValue, is("test"));

    }

    @Test
    public void testGetSecretValue_withoutSecretFile_shouldCreateSecretFile() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        String secretValue = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getTestSecretFile()),
            getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        String newPasswordValue = props.getProperty("mySecretPassword");
        assertThat(newPasswordValue, is(not("test")));
        assertThat(Encryption.isEncryptedPassword(newPasswordValue), is(true));
        assertThat(getTestSecretFile().exists(), is(true));

    }

    @Test
    public void testGetSecretValue_withoutSecretFile_shouldNotCreateSecretFile() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        Exception expectException = TestUtils.expectException(() -> SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withSecretFile(getTestSecretFile()).disableAutoCreateSecretKey(),
            getTestPropertyFile(), "mySecretPassword"));
        // validate result
        assertThat(expectException.getMessage(), containsString("test.key"));
        assertThat(expectException.getMessage(), containsString("doesn't exist, and auto create is off"));

        // validate that property file is unchanged
        assertThat(getTestSecretFile().exists(), is(false));

    }

    private File getTestSecretFile() {
        return new File("./target/tests/test.key");
    }

    private void writeProperties(final File testPropertyFile, final String... line) throws IOException {
        FileUtils.writeLines(testPropertyFile, StandardCharsets.ISO_8859_1.name(), Arrays.asList(line));
    }

    private File getTestPropertyFile() {
        return new File("./target/tests/test.properties");
    }

    private File getSecretFileExample() {
        return new File("./src/test/data/secretFileExample.key");
    }

}
