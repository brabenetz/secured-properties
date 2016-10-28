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
package net.brabenetz.lib.security.properties.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import net.brabenetz.lib.security.properties.test.TestUtils;

public class SecretContainerStoreTest {

    private final File testFolder = new File("./target/tests/" + getClass().getSimpleName());
    private final File secretContainerFile = new File(this.testFolder, "test.key");

    @Before
    public void cleanupTestFolder() throws IOException {
        if (this.testFolder.exists()) {
            FileUtils.forceDelete(this.testFolder);
        }
    }

    @Test
    public void testWriteAndRead_withTypicalWorkflows_HappPath() throws Exception {
        List<String> storedLines;

        // pre condtion
        assertThat(this.secretContainerFile.exists(), is(false));

        // start Test
        SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.AES_128);
        assertThat(this.secretContainerFile.exists(), is(true));

        // validate Result
        storedLines = FileUtils.readLines(this.secretContainerFile, StandardCharsets.UTF_8);

        assertThat(storedLines.size(), is(2));
        assertThat(storedLines.get(0), is("AES_128"));

        String firstSecretKeyStr = storedLines.get(1);

        // start second Test-Run with existing file.
        SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.AES_128);

        storedLines = FileUtils.readLines(this.secretContainerFile, StandardCharsets.UTF_8);

        assertThat(storedLines.size(), is(2));
        assertThat(storedLines.get(0), is("AES_128"));
        assertThat(storedLines.get(1), is(firstSecretKeyStr)); // unchanged Key

        // start third Test-Run without existing file.
        this.secretContainerFile.delete();
        SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.AES_128);

        storedLines = FileUtils.readLines(this.secretContainerFile, StandardCharsets.UTF_8);

        assertThat(storedLines.size(), is(2));
        assertThat(storedLines.get(0), is("AES_128"));
        assertThat(storedLines.get(1), is(not(firstSecretKeyStr))); // new generated Key must be different.
    }

    @Test
    public void testWrite_WithUnsupportedAlgorithm() throws Exception {

        // run test
        Exception expectException = TestUtils
            .expectException(() -> SecretContainerStore.getSecretContainer(this.secretContainerFile, true, new MockAlgorithm("test", 1)));

        // validate Exception message
        assertThat(expectException.getMessage(), containsString("No supported Algorithm found in"));
        assertThat(expectException.getMessage(), containsString("test_1"));
    }

    @Test
    public void testRead_withMultipleSupportedAlgorithm() throws Exception {
        // will create the secretFile with Algorithm AES_128
        SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.AES_128);

        // start Test
        SecretContainer secretContainer = SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.values());
        assertThat(secretContainer.getAlgorithm(), is(SupportedAlgorithm.AES_128));

    }

    @Test
    public void testRead_withUnsupportedAlgorithm() throws Exception {
        // will create the secretFile with Algorithm AES_128
        SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.AES_128);

        Exception expectException = TestUtils
            .expectException(() -> SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.DESede_112));

        // validate Exception message
        assertThat(expectException.getMessage(), containsString("Unable to parse algorithm"));
        assertThat(expectException.getMessage(), containsString("AES_128"));
        assertThat(expectException.getMessage(), containsString("DESede_112"));

    }

    @Test
    public void testRead_invalidSecretFile_notEnoughLines() throws Exception {
        // will create the secretFile with Algorithm AES_128
        FileUtils.writeLines(this.secretContainerFile, Collections.singletonList("single-Line"));

        Exception expectException = TestUtils
            .expectException(() -> SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.DESede_112));

        // validate Exception message
        assertThat(expectException.getMessage(), containsString("secrete File must have at least two lines"));

    }

    @Test
    public void testRead_invalidSecretFile_notParsableSecretKey() throws Exception {
        // will create the secretFile with Algorithm AES_128
        FileUtils.writeLines(this.secretContainerFile, Arrays.asList("AES_128", "INVALID-KEY"));

        Exception expectException = TestUtils
            .expectException(() -> SecretContainerStore.getSecretContainer(this.secretContainerFile, true, SupportedAlgorithm.AES_128));

        // validate Exception message
        assertThat(expectException.getMessage(), containsString("The secret key could not be read from File"));
        assertThat(expectException.getMessage(), containsString(this.secretContainerFile.getName()));

    }

    @Test
    public void testRead_secredFileDoesntExistAndAutoCreateIsOf() throws Exception {
        // will create the secretFile with Algorithm AES_128

        Exception expectException = TestUtils
            .expectException(() -> SecretContainerStore.getSecretContainer(this.secretContainerFile, false, SupportedAlgorithm.AES_128));

        // validate Exception message
        assertThat(expectException.getMessage(), containsString("Secret file"));
        assertThat(expectException.getMessage(), containsString(this.secretContainerFile.getName()));
        assertThat(expectException.getMessage(), containsString("doesn't exist, and auto create is off"));

    }
}
