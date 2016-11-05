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

import com.github.fge.lambdas.Throwing;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Some Utilities about encryptions.
 */
public final class Encryption {
    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Encryption.class);
    private static final Pattern ENCRYPTED_VALUE = Pattern.compile("^\\{([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)\\}$");

    private Encryption() {
        super();
    }
    /**
     * Checks if the given {@link Algorithm} can be used to encrypt String-Values.
     * 
     * @param algorithm
     *        The {@link Algorithm} to check.
     * @return true if the {@link Algorithm} is supported.
     */
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

    /**
     * @param algorithms
     *        a list of {@link Algorithm} to check
     * @return the first {@link Algorithm} which can be used for encryption, see
     *         {@link #isAlgorithmSupported(Algorithm)}.
     * @throws IllegalArgumentException
     *         if no {@link Algorithm} is supported.
     */
    public static Algorithm getFirstSupportedAlgorithm(final Algorithm... algorithms) throws IllegalArgumentException {
        for (Algorithm algorithm : algorithms) {
            if (isAlgorithmSupported(algorithm)) {
                return algorithm;
            }
        }
        throw new IllegalArgumentException("No supported Algorithm found in: " + Arrays.asList(algorithms));
    }

    /**
     * generate a {@link SecretKey} with the given {@link Algorithm} and wraps checked Exceptions
     * into RuntimeExceptions.
     * 
     * @param algorithm
     *        the {@link Algorithm} to use for generated the {@link SecretKey}
     * @return the generated {@link SecretKey}.
     */
    public static SecretKey createKey(final Algorithm algorithm) {
        KeyGenerator kg = Throwing.supplier(() -> KeyGenerator.getInstance(algorithm.getKey())).get();
        kg.init(algorithm.getSize());
        return kg.generateKey();
    }

    /**
     * Read a {@link SecretKey} from a base64 encoded String.
     * 
     * @see #toBase64String(SecretKey)
     */
    public static SecretKey readSecretKey(final Algorithm algorithm, final String secretKeyBase64) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyBase64);
        return new SecretKeySpec(secretKeyBytes, algorithm.getKey());
    }

    /**
     * transform a {@link SecretKey} to a {@link Base64} encoded String.
     */
    public static String toBase64String(final SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Checks if the given String looks like an encrypted value.
     */
    public static boolean isEncryptedValue(final String mybeEncryptedValue) {
        if (StringUtils.isEmpty(mybeEncryptedValue)) {
            return false;
        }
        return ENCRYPTED_VALUE.matcher(mybeEncryptedValue).matches();
    }

    /**
     * Encrypt the given plain-text value with the given {@link SecretKey} and the given {@link Algorithm}.
     * 
     * @param algorithm
     *        The {@link Algorithm} to use for the encryption.
     * @param secretKey
     *        The {@link SecretKey} to use for the encryption.
     * @param plainTextValue
     *        The value which should be encrypted.
     * @return the encrypted value.
     */
    public static String encrypt(final Algorithm algorithm, final SecretKey secretKey, final String plainTextValue) {
        byte[] valueBytes = plainTextValue.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedValue = Throwing.supplier(() -> encrypt(algorithm, secretKey, valueBytes)).get();
        return "{" + Base64.getEncoder().encodeToString(encryptedValue) + "}";
    }

    /**
     * Decrypt the given encrypted value with the given {@link SecretKey} and the given {@link Algorithm}.
     * 
     * @param algorithm
     *        The {@link Algorithm} to use for the decryption.
     * @param secretKey
     *        The {@link SecretKey} to use for the decryption.
     * @param encryptedValue
     *        the encrypted value to decrypt.
     * @return the decrypted value.
     */
    public static String decrypt(final Algorithm algorithm, final SecretKey secretKey, final String encryptedValue) {
        byte[] encryptedValueBytes = Base64.getDecoder().decode(StringUtils.strip(encryptedValue, "{}"));
        byte[] valueBytes = Throwing.supplier(() -> decrypt(algorithm, secretKey, encryptedValueBytes)).get();
        return new String(valueBytes, StandardCharsets.UTF_8);
    }

    private static byte[] encrypt(final Algorithm algorithm, final SecretKey secretKey, final byte[] valueBytes) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(algorithm.getKey());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(valueBytes);
    }

    private static byte[] decrypt(final Algorithm algorithm, final SecretKey secretKey, final byte[] valueBytes) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(algorithm.getKey());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(valueBytes);
    }
}
