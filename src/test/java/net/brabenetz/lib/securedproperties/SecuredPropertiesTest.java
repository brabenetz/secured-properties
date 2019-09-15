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
package net.brabenetz.lib.securedproperties;

import net.brabenetz.lib.securedproperties.test.TestUtils;
import net.brabenetz.lib.securedproperties.utils.SecuredPropertiesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

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
    public void testUtilityPattern() {
        // this test is more for test-coverage than logic :D
        Assert.assertTrue(TestUtils.isDefaultConstructorHidden(SecuredProperties.class));
    }

    @Test
    public void testGetSecretValue_withMultipleFiles_shouldIgnoreNotExistingOnes() throws Exception {
        // run test
        final String secretValue = SecuredProperties.getSecretValue(
                new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
                new File[] {
                        new File("./conf-abc/application.properties"),
                        new File("./src/test/data/TestProperties-Valid.properties"),
                        new File("./conf-xyz/application.properties"),
                },
                "mySecretPassword");

        // validate result
        assertThat(secretValue, is("test"));

    }

    @Test
    public void testGetSecretValue_withSecretFileFromProperty() throws Exception {
        // run test
        final String secretValue = SecuredProperties.getSecretValue(
                new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
                new File("./src/test/data/TestProperties-Valid.properties"), "mySecretPassword");

        // validate result
        assertThat(secretValue, is("test"));

    }

    @Test
    public void testGetSecretValue_fromEncryptedPropertyFile() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword={buMkr+yZH9RclafjETtlSQ==}");

        // run test
        final String secretValue = SecuredProperties.getSecretValue(
                new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
                getTestPropertyFile(), "mySecretPassword");

        // validate result
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        final Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        assertThat(props.get("mySecretPassword"), is("{buMkr+yZH9RclafjETtlSQ==}"));

    }

    @Test
    public void testGetSecretValue_notDefinedProperty_shouldReturnNull() throws Exception {
        // prepare SystemProperty
        writeProperties(getTestPropertyFile(), "title=Some Test");

        // run test
        final String secretValue = SecuredProperties.getSecretValue(
                new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
                getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is(nullValue()));

    }

    @Test
    public void testManualExampleEncryptAndDecrypt_fromEncryptedSystemProperty() throws Exception {
        // prepare SystemProperty
        System.setProperty("mySecretPassword", "{buMkr+yZH9RclafjETtlSQ==}");

        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig().withSecretFile(getSecretFileExample());

        final String encryptedValue = checkSystemProperties(config, "mySecretPassword");

        // validate result
        assertThat(encryptedValue, is("test"));

    }

    @Test
    public void testManualExampleEncryptAndDecrypt_fromUnencryptedSystemProperty_shouldLogInfoMessage() throws Exception {
        // prepare SystemProperty
        System.setProperty("mySecretPassword", "test");

        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig().withSecretFile(getSecretFileExample());
        final String encryptedValue = checkSystemProperties(config, "mySecretPassword");

        // validate result
        assertThat(encryptedValue, is("test"));

        // TODO brabenetz 28.10.2016 : validate Log Message
    }

    @Test
    public void testGetSecretValue_fromUnencryptedPropertyFile_shouldNotReplaceProperty() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        final String secretValue = SecuredProperties.getSecretValue(
                new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()),
                getTestPropertyFile(), "mySecretPassword");
        // validate result
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        final Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        final String newPasswordValue = props.getProperty("mySecretPassword");
        assertThat(newPasswordValue, is("test"));

    }

    @Test
    public void testGetSecretValue_withoutSecretFile_shouldCreateSecretFile() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig().withSecretFile(getTestSecretFile());
        // validate result
        final String secretValue = SecuredProperties.getSecretValue(config, getTestPropertyFile(), "mySecretPassword");
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        final Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        final String newPasswordValue = props.getProperty("mySecretPassword");
        assertThat(newPasswordValue, is("test"));
        assertThat(SecuredProperties.isEncryptedValue(newPasswordValue), is(false));
        assertThat(getTestSecretFile().exists(), is(true));

    }

    @Test
    public void testEncryptNonEncryptedValues_withMultipleFiles_shouldIgnoreNotExistingOnes() throws Exception {
        writeProperties(getTestPropertyFile(), "pwd1=test");
        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig().withSecretFile(getSecretFileExample());
        SecuredProperties.encryptNonEncryptedValues(
                config,
                new File[] {
                        new File("./conf-abc/application.properties"),
                        getTestPropertyFile(),
                        new File("./conf-xyz/application.properties"),
                },
                "pwd1", "pwd2", "pwd3");

        // validate result
        final String secretValue = SecuredProperties.getSecretValue(
                config, getTestPropertyFile(), "pwd1");
        assertThat(secretValue, is("test"));

        // validate that property file is changed
        final Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        assertThat(props.getProperty("pwd1"), is(not("test")));
        assertThat(SecuredProperties.isEncryptedValue(props.getProperty("pwd1")), is(true));

    }

    @Test
    public void testEncryptNonEncryptedValues_multipleValues_shouldReplaceUnencryptedProperties() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "pwd1=test", "pwd2={buMkr+yZH9RclafjETtlSQ==}", "pwd3=test");

        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig().withSecretFile(getSecretFileExample());
        SecuredProperties.encryptNonEncryptedValues(
                config, getTestPropertyFile(), "pwd1", "pwd2", "pwd3", "pwd99");
        final Map<String, String> secretValues = SecuredProperties.getSecretValues(
                config, getTestPropertyFile(), "pwd1", "pwd2", "pwd3", "pwd99");
        // validate result
        assertThat(secretValues.get("pwd1"), is("test"));
        assertThat(secretValues.get("pwd2"), is("test"));
        assertThat(secretValues.get("pwd3"), is("test"));
        assertThat(secretValues.get("pwd99"), is(nullValue()));

        // validate that property file is unchanged
        final Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        assertThat(props.getProperty("pwd1"), is(not("test")));
        assertThat(props.getProperty("pwd2"), is("{buMkr+yZH9RclafjETtlSQ==}"));
        assertThat(props.getProperty("pwd3"), is(not("test")));
        assertThat(SecuredProperties.isEncryptedValue(props.getProperty("pwd1")), is(true));
        assertThat(SecuredProperties.isEncryptedValue(props.getProperty("pwd2")), is(true));
        assertThat(SecuredProperties.isEncryptedValue(props.getProperty("pwd3")), is(true));

    }

    @Test
    public void testEncryptNonEncryptedValues_fromUnencryptedPropertyFileWithoutSalt_shouldReplacePropertyWithAlwaysSameValue()
            throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig().withSecretFile(getSecretFileExample()).withSaltLength(0);
        SecuredProperties.encryptNonEncryptedValues(config, getTestPropertyFile(), "mySecretPassword");

        // validate result
        final String secretValue = SecuredProperties.getSecretValue(config, getTestPropertyFile(), "mySecretPassword");
        assertThat(secretValue, is("test"));

        // validate that property file contains the encrypted value but without salt it will result always with the same value.
        final Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        final String newPasswordValue = props.getProperty("mySecretPassword");
        assertThat(newPasswordValue, is("{wNnuFmepE9cAN6GpaULDZw==}"));

    }

    @Test
    public void testEncryptNonEncryptedValues_withoutSecretFile_shouldCreateSecretFile() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig().withSecretFile(getTestSecretFile());
        SecuredProperties.encryptNonEncryptedValues(config, getTestPropertyFile(), "mySecretPassword");
        assertThat(getTestSecretFile().exists(), is(true));

        // validate result
        final String secretValue = SecuredProperties.getSecretValue(config, getTestPropertyFile(), "mySecretPassword");
        assertThat(secretValue, is("test"));

        // validate that property file is unchanged
        final Properties props = SecuredPropertiesUtils.readProperties(getTestPropertyFile());
        final String newPasswordValue = props.getProperty("mySecretPassword");
        assertThat(newPasswordValue, is(not("test")));
        assertThat(SecuredProperties.isEncryptedValue(newPasswordValue), is(true));
        assertThat(getTestSecretFile().exists(), is(true));

    }

    @Test
    public void testEncryptNonEncryptedValues_withoutSecretFile_shouldNotCreateSecretFile() throws Exception {
        // prepare property File
        writeProperties(getTestPropertyFile(), "mySecretPassword=test");

        // run test
        final SecuredPropertiesConfig config = new SecuredPropertiesConfig()
                .withSecretFile(getTestSecretFile())
                .withAutoCreateSecretKey(false);
        final Exception expectException = TestUtils.expectException(
                () -> SecuredProperties.encryptNonEncryptedValues(config, getTestPropertyFile(), "mySecretPassword"));
        // validate result
        assertThat(expectException.getMessage(), containsString("test.key"));
        assertThat(expectException.getMessage(), containsString("doesn't exist, and auto create is off"));

        // validate that property file is unchanged
        assertThat(getTestSecretFile().exists(), is(false));

    }

    private String checkSystemProperties(final SecuredPropertiesConfig config, final String key) {

        final String systemPropPassword = System.getProperty(key);
        if (SecuredProperties.isEncryptedValue(systemPropPassword)) {
            return SecuredProperties.decrypt(config, systemPropPassword);
        } else if (StringUtils.isNotEmpty(systemPropPassword)) {
            System.out.println(String.format("you could now use the following encrypted password: -D%s=%s", key,
                    SecuredProperties.encrypt(config, systemPropPassword)));
            return systemPropPassword;
        } else {
            return null;
        }
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
