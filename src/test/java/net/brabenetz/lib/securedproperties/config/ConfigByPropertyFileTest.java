/*-
 * #%L
 * Secured Properties
 * ===============================================================
 * Copyright (C) 2016 - 2019 Brabenetz Harald, Austria
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
package net.brabenetz.lib.securedproperties.config;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ConfigByPropertyFileTest {

    @Before
    public void before() throws Exception {
        if (getTestPropertyFile().exists()) {
            FileUtils.forceDelete(getTestPropertyFile());
        }

    }

    @Test
    public void testGetValue() throws Exception {

        // prepare property File
        writeProperties(getTestPropertyFile(), "secured-properties.secret-file=Test-value-for-Property-File-UnitTest");

        ConfigByPropertyFile configInitializer = new ConfigByPropertyFile(getTestPropertyFile());

        assertThat(configInitializer.getValue(ConfigKey.SECRET_FILE), is(equalTo("Test-value-for-Property-File-UnitTest")));
        assertThat(configInitializer.getValue(ConfigKey.AUTO_ENCRYPT_NON_ENCRYPTED_VALUES), is(nullValue()));
    }

    @Test
    public void testGetValue_fromNotExistingFile_shouldWorkWithNOP() throws Exception {

        // prepare property File
        writeProperties(getTestPropertyFile(), "secured-properties.secret-file=Test-value-for-Property-File-UnitTest");

        ConfigByPropertyFile configInitializer = new ConfigByPropertyFile(new File("./doesnt-exist.properties"));

        assertThat(configInitializer.getValue(ConfigKey.SECRET_FILE), is(nullValue()));
        assertThat(configInitializer.getValue(ConfigKey.AUTO_ENCRYPT_NON_ENCRYPTED_VALUES), is(nullValue()));
    }

    private void writeProperties(final File testPropertyFile, final String... line) throws IOException {
        FileUtils.writeLines(testPropertyFile, StandardCharsets.ISO_8859_1.name(), Arrays.asList(line));
    }
    private File getTestPropertyFile() {
        return new File("./target/tests/test.properties");
    }

}
