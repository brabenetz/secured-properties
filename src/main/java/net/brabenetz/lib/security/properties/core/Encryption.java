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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import com.github.fge.lambdas.Throwing;

public class Encryption {
    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Encryption.class);
    private static final Pattern ENCRYPTED_PASSWORD = Pattern.compile("^\\{([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)\\}$");

    public static boolean isAlgorithmSupported(final Algorithm algorithm) {
        try {
            encrypt(algorithm, createKey(algorithm), "test");
            Cipher.getInstance(algorithm.getKey());
            return true;
        } catch (Exception e) {
            // An exception here probably means the JCE provider hasn't
            // been permanently installed on this system by listing it
            // in the $JAVA_HOME/jre/lib/security/java.security file.
            LOG.info("Algorithm {} is not supported: {}", algorithm, e.getMessage());
            return false;
        }
    }

    public static Algorithm getFirstSupportedAlgorithm(final Algorithm... algorithms) {
        for (Algorithm algorithm : algorithms) {
            if (isAlgorithmSupported(algorithm)) {
                return algorithm;
            }
        }
        throw new IllegalStateException("No supported Algorithm found in: " + Arrays.asList(algorithms));
    }

    public static SecretKey createKey(final Algorithm algorithm) {
        KeyGenerator kg = Throwing.supplier(() -> KeyGenerator.getInstance(algorithm.getKey())).get();
        kg.init(algorithm.getSize());
        return kg.generateKey();
    }

    public static SecretKey readSecretKey(final Algorithm algorithm, final String secretKeyBase64) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyBase64);
        return new SecretKeySpec(secretKeyBytes, algorithm.getKey());
    }

    public static String toString(final SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static boolean isEncryptedPassword(final String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        return ENCRYPTED_PASSWORD.matcher(password).matches();
    }

    public static String encrypt(final Algorithm algorithm, final SecretKey secretKey, final String password) {
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedPassword = Throwing.supplier(() -> encrypt(algorithm, secretKey, passwordBytes)).get();
        return "{" + Base64.getEncoder().encodeToString(encryptedPassword) + "}";
    }

    public static String decrypt(final Algorithm algorithm, final SecretKey secretKey, final String encryptedPassword) {
        byte[] encryptedPasswordBytes = Base64.getDecoder().decode(StringUtils.strip(encryptedPassword, "{}"));
        byte[] passwordBytes = Throwing.supplier(() -> decrypt(algorithm, secretKey, encryptedPasswordBytes)).get();
        return new String(passwordBytes, StandardCharsets.UTF_8);
    }

    private static byte[] encrypt(final Algorithm algorithm, final SecretKey secretKey, final byte[] passwordBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm.getKey());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(passwordBytes);
    }

    private static byte[] decrypt(final Algorithm algorithm, final SecretKey secretKey, final byte[] passwordBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm.getKey());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(passwordBytes);
    }
}
