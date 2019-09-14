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

import net.brabenetz.lib.securedproperties.core.Encryption;
import net.brabenetz.lib.securedproperties.core.SecretContainer;
import net.brabenetz.lib.securedproperties.core.SecretContainerStore;
import net.brabenetz.lib.securedproperties.utils.SecuredPropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Encrypt and decrypt secret values in properties files with a secret key.
 * <p>
 * <b>Example:</b> <br>
 * The Property file "myConfiguration.properties":
 *
 * <pre>
 * mySecretPassword = test
 * </pre>
 *
 * The Java code:
 *
 * <pre>
 * String secretValue = SecuredProperties.getSecretValue(
 *     new SecuredPropertiesConfig().withSecretFile(new File("G:/mysecret.key")), // custom config
 *     new File("myConfiguration.properties"), // The Property File
 *     "mySecretPassword"); // the property-key from "myConfiguration.properties"
 * </pre>
 *
 * will return "test" as secretValue and automatically encrypt the value in the property file. After
 * the first run the Property file will looks similar to the following:
 *
 * <pre>
 *  mySecretPassword = {wVtvW8lQrwCf8MA9sadwww==}
 * </pre>
 *
 * This encrypted password can now be read only in combination with the secret file
 * "G:/mysecret.key"
 */
public final class SecuredProperties {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SecuredProperties.class);

    private SecuredProperties() {
        super();
    }

    /**
     *
     * @see SecuredProperties
     * @param config the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param propertyFile The Properties file to with the encrypted value
     * @param key The Property-Key of the encrypted value.
     * @return The decrypted plain-text value.
     */
    public static String getSecretValue(final SecuredPropertiesConfig config, final File propertyFile, final String key) {
        return getSecretValues(config, propertyFile, key).get(key);
    }

    /**
     * @see SecuredProperties
     * @param config        the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param propertyFiles A list of Property file to with the encrypted value
     * @param key           The Property-Key of the encrypted value.
     * @return The decrypted plain-text value.
     */
    public static String getSecretValue(final SecuredPropertiesConfig config, final File[] propertyFiles, final String key) {
        return getSecretValues(config, propertyFiles, key).get(key);
    }

    /**
     * @see SecuredProperties
     * @param config       the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param propertyFile The Properties file to with the encrypted value
     * @param keys         The Property-Keys of the encrypted value.
     * @return A Map with the decrypted plain-text values per key.
     */
    public static Map<String, String> getSecretValues(
            final SecuredPropertiesConfig config, final File propertyFile, final String... keys) {
        return getSecretValues(config, new File[] {propertyFile}, keys);
    }

    /**
     * @see SecuredProperties
     * @param config        the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param propertyFiles A list of Property file to with the encrypted value
     * @param keys          The Property-Keys of the encrypted value.
     * @return A Map with the decrypted plain-text values per key.
     */
    public static Map<String, String> getSecretValues(
            final SecuredPropertiesConfig config, final File[] propertyFiles, final String... keys) {

        Map<String, String> result = new HashMap<>();

        for (File propertyFile : propertyFiles) {
            if (!propertyFile.exists()) {
                continue;
            }
            Map<String, String> unencryptedValues = new HashMap<>();
            final Properties properties = SecuredPropertiesUtils.readProperties(propertyFile);
            final SecretContainer secretContainer = getSecretContainer(config);
            for (String key : keys) {

                String value = properties.getProperty(key);
                if (Encryption.isEncryptedValue(value)) {
                    // read and decrypt value
                    result.put(key, Encryption.decrypt(secretContainer.getAlgorithm(), secretContainer.getSecretKey(), config.getSaltLength(), value));
                } else if (StringUtils.isNotEmpty(value)) {
                    // replace value with encrypted in property file.
                    unencryptedValues.put(key, value);
                    result.put(key, value);
                } else {
                    result.put(key, null);
                }
            }

            if (config.isAutoEncryptNonEncryptedValues()) {
                Map<String, String> encryptedValues = encryptValues(config, secretContainer, unencryptedValues);
                Pair<String, String>[] newProperties = encryptedValues.entrySet().stream()
                        .map(e -> Pair.of(e.getKey(), e.getValue()))
                        .collect(Collectors.toSet())
                        .toArray(new Pair[encryptedValues.size()]);
                SecuredPropertiesUtils.replaceSecretValue(propertyFile, newProperties);
            } else if (!unencryptedValues.isEmpty()) {
                LOG.warn("AutoEncryptNonEncryptedValues is off. Secret values in Property file '{}' will remain plain-text.", propertyFile);
            }
        }

        return result;

    }

    private static Map<String, String> encryptValues(final SecuredPropertiesConfig config, final SecretContainer secretContainer,
        final Map<String, String> unencryptedValues) {

        Map<String, String> encryptedValues = new HashMap<>();
        for (Entry<String, String> entry : unencryptedValues.entrySet()) {
            encryptedValues.put(entry.getKey(), Encryption.encrypt(
                    secretContainer.getAlgorithm(), secretContainer.getSecretKey(), config.getSaltLength(), entry.getValue()));
        }
        return encryptedValues;
    }

    /**
     * Checks if the given String looks like an encrypted value.
     */
    public static boolean isEncryptedValue(final String maybeEncryptedValue) {
        return Encryption.isEncryptedValue(maybeEncryptedValue);
    }

    /**
     * Encrypt the given value (will create the secret key if not already exist).
     *
     * @param config
     *        the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param plainTextValue
     *        The value to encrypt
     * @return the encrypted value.
     */
    public static String encrypt(final SecuredPropertiesConfig config, final String plainTextValue) {
        final SecretContainer secretContainer = getSecretContainer(config);

        return Encryption.encrypt(secretContainer.getAlgorithm(), secretContainer.getSecretKey(), config.getSaltLength(), plainTextValue);
    }

    /**
     * Encrypt the given password (will create the secret key if not already exist).
     *
     * @param config the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param encryptedPassword The password to encrypt
     * @return the encrypted value.
     */
    public static String decrypt(final SecuredPropertiesConfig config, final String encryptedPassword) {
        final SecretContainer secretContainer = getSecretContainer(config);

        return Encryption.decrypt(secretContainer.getAlgorithm(), secretContainer.getSecretKey(), config.getSaltLength(), encryptedPassword);

    }

    private static SecretContainer getSecretContainer(final SecuredPropertiesConfig config) {

        return SecretContainerStore.getSecretContainer(config.getSecretFile(), config.isAutoCreateSecretKey(), config.getAllowedAlgorithm());
    }
}
