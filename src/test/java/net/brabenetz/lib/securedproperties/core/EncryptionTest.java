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
package net.brabenetz.lib.securedproperties.core;

import net.brabenetz.lib.securedproperties.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EncryptionTest {

    @Test
    public void testIsAlgorithmSupported() throws Exception {
        Assert.assertTrue(Encryption.isAlgorithmSupported(SupportedAlgorithm.AES_128));
        Assert.assertTrue(Encryption.isAlgorithmSupported(SupportedAlgorithm.DESede_112));
        Assert.assertTrue(Encryption.isAlgorithmSupported(SupportedAlgorithm.DESede_168));
        Assert.assertFalse(Encryption.isAlgorithmSupported(new MockAlgorithm("test", 1)));
        Assert.assertFalse(Encryption.isAlgorithmSupported(new MockAlgorithm("AES", 1000)));
        // By default, Java only supports AES 128 (travis-ci supports AES_256), but you can import it into your jvm
        // Assert.assertFalse(Encryption.isAlgorithmSupported(SupportedAlgorithm.AES_256));
    }

    @Test
    public void testUtilityPattern() {
        // this test is more for test-coverage than logic :D
        Assert.assertTrue(TestUtils.isDefaultConstructorHidden(Encryption.class));
    }

    @Test
    public void testGetFirstSupportedAlgorithm() throws Exception {
        assertThat(Encryption.getFirstSupportedAlgorithm(new MockAlgorithm("test", 1), SupportedAlgorithm.AES_128), is(SupportedAlgorithm.AES_128));
    }

    @Test
    public void testCreateKey_Success() throws Exception {
        Assert.assertNotNull(Encryption.createKey(SupportedAlgorithm.AES_128));
        Assert.assertNotNull(Encryption.createKey(SupportedAlgorithm.AES_192));
        Assert.assertNotNull(Encryption.createKey(SupportedAlgorithm.AES_256));
        Assert.assertNotNull(Encryption.createKey(SupportedAlgorithm.DESede_112));
        Assert.assertNotNull(Encryption.createKey(SupportedAlgorithm.DESede_168));
    }

    @Test
    public void testCreateKey_Failed() throws Exception {
        Exception exc = TestUtils.expectException(() -> Encryption.createKey(new MockAlgorithm("AES", 1000)));
        // exception message: "Wrong keysize: must be equal to 128, 192 or 256"
        assertThat(exc.getMessage(), containsString("128"));
        assertThat(exc.getMessage(), containsString("192"));
        assertThat(exc.getMessage(), containsString("256"));
    }

    @Test
    public void testEncryptDecryptRoundup() throws Exception {
        // an example password with some special chars.
        String password = "abc123!\"§$%&/()=?";

        SupportedAlgorithm[] values = SupportedAlgorithm.values();
        for (SupportedAlgorithm algorithm : values) {
            if (Encryption.isAlgorithmSupported(algorithm)) {
                testEncryptDecryptRoundup(algorithm, password);
            }
        }

    }

    private void testEncryptDecryptRoundup(final SupportedAlgorithm algorithm, final String password) {
        final int saltLength = 11;
        Assert.assertFalse(Encryption.isEncryptedValue(password));
        String secretKeyStr = Encryption.toBase64String(Encryption.createKey(algorithm));
        String encryptPw = Encryption.encrypt(algorithm, Encryption.readSecretKey(algorithm, secretKeyStr), saltLength, password);
        Assert.assertTrue(Encryption.isEncryptedValue(encryptPw));
        String finalPassword = Encryption.decrypt(algorithm, Encryption.readSecretKey(algorithm, secretKeyStr), saltLength, encryptPw);
        assertThat(finalPassword, is(password));
    }

    @Test
        public void testIsEncryptedValue() throws Exception {
            Assert.assertFalse(Encryption.isEncryptedValue(null));
        }
}
