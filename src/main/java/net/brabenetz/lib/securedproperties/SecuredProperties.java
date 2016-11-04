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

import java.io.File;
import java.util.Properties;

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
        final Properties properties = SecuredPropertiesUtils.readProperties(propertyFile);
        final SecretContainer secretContainer = getSecretContainer(config, properties);

        String value = properties.getProperty(key);
        if (Encryption.isEncryptedPassword(value)) {
            // read and decrypt
            return Encryption.decrypt(secretContainer.getAlgorithm(), secretContainer.getSecretKey(), value);
        } else if (StringUtils.isNotEmpty(value)) {
            // replace password with encrypted in property file.
            if (config.isAutoEncryptNonEncryptedValues()) {
                String encryptedPassword = Encryption.encrypt(secretContainer.getAlgorithm(), secretContainer.getSecretKey(), value);
                SecuredPropertiesUtils.replaceSecretValue(propertyFile, key, encryptedPassword);
            } else {
                LOG.warn("AutoEncryptNonEncryptedValues is off. Value in Property file will remain plain-text.");
            }
            return value;
        }

        return null;

    }
    
    public static boolean isEncryptedPassword(final String password) {
        return Encryption.isEncryptedPassword(password);
    }

    /**
     * @see #encrypt(SecuredPropertiesConfig, File, String)
     */
    public static String encrypt(final SecuredPropertiesConfig config, final String password) {
        return encrypt(config, null, password);
    }
    
    /**
     * Encrypt the given password (will create the secret key if not already exist).
     * 
     * @param config the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param propertyFile The optional PropertyFile is only used if {@link SecuredPropertiesConfig#withSecretFilePropertyKey(String)} is set.
     * @param password The password to encrypt
     * @return the encrypted value.
     */
    public static String encrypt(final SecuredPropertiesConfig config, final File propertyFile, final String password) {
        final Properties properties = SecuredPropertiesUtils.readProperties(propertyFile);
        final SecretContainer secretContainer = getSecretContainer(config, properties);
        
        return Encryption.encrypt(secretContainer.getAlgorithm(), secretContainer.getSecretKey(), password);
    }
    
    /**
     * @see #decrypt(SecuredPropertiesConfig, File, String)
     */
    public static String decrypt(final SecuredPropertiesConfig config, final String encryptedPassword) {
        return decrypt(config, null, encryptedPassword);
    }

    
    /**
     * Encrypt the given password (will create the secret key if not already exist).
     * 
     * @param config the {@link SecuredPropertiesConfig} to control custom behavior.
     * @param propertyFile The optional PropertyFile is only used if {@link SecuredPropertiesConfig#withSecretFilePropertyKey(String)} is set.
     * @param encryptedPassword The password to encrypt
     * @return the encrypted value.
     */
    public static String decrypt(final SecuredPropertiesConfig config, final File propertyFile, final String encryptedPassword) {
        final Properties properties = SecuredPropertiesUtils.readProperties(propertyFile);
        final SecretContainer secretContainer = getSecretContainer(config, properties);
        
        return Encryption.decrypt(secretContainer.getAlgorithm(), secretContainer.getSecretKey(), encryptedPassword);
        
    }

    private static SecretContainer getSecretContainer(final SecuredPropertiesConfig config, final Properties properties) {
        File secretFile = SecuredPropertiesUtils.getSecretFile(config.getSecretFile(), config.getSecretFilePropertyKey(), properties);

        return SecretContainerStore.getSecretContainer(secretFile, config.isAutoCreateSecretKey(), config.getAllowedAlgorithm());
    }
}
