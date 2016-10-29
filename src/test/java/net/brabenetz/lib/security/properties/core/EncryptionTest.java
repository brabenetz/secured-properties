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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Test;

import net.brabenetz.lib.security.properties.test.TestUtils;

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
        // System.out.println(exc.getMessage());
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
            System.out.println("Algorithm: " + algorithm.name());
            if (Encryption.isAlgorithmSupported(algorithm)) {
                testEncryptDecryptRoundup(algorithm, password);
            }
        }

    }

    private void testEncryptDecryptRoundup(final SupportedAlgorithm algorithm, final String password) {
        Assert.assertFalse(Encryption.isEncryptedPassword(password));
        String secretKeyStr = Encryption.toString(Encryption.createKey(algorithm));
        String encryptPw = Encryption.encrypt(algorithm, Encryption.readSecretKey(algorithm, secretKeyStr), password);
        Assert.assertTrue(Encryption.isEncryptedPassword(encryptPw));
        String finalPassword = Encryption.decrypt(algorithm, Encryption.readSecretKey(algorithm, secretKeyStr), encryptPw);
        assertThat(finalPassword, is(password));
    }

    @Test
    public void testIsEncryptedPassword() throws Exception {
        Assert.assertFalse(Encryption.isEncryptedPassword(null));
    }
}
