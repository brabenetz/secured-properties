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

import net.brabenetz.lib.securedproperties.config.Config;
import net.brabenetz.lib.securedproperties.config.ConfigInitializer;
import net.brabenetz.lib.securedproperties.config.ConfigInitializers;
import net.brabenetz.lib.securedproperties.core.Algorithm;
import net.brabenetz.lib.securedproperties.core.SupportedAlgorithm;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * Configuration Object to control the behavior of {@link SecuredProperties}.
 */
public class SecuredPropertiesConfig implements Config {

    private static final Logger LOG = LoggerFactory.getLogger(SecuredPropertiesConfig.class);

    private static final int DEFAULT_SALT_LENGTH = 11;

    private File secretFile;
    private int saltLength = DEFAULT_SALT_LENGTH;
    private Algorithm[] allowedAlgorithm = new Algorithm[] {
            SupportedAlgorithm.AES_256,
            SupportedAlgorithm.AES_192,
            SupportedAlgorithm.AES_128,
            SupportedAlgorithm.DESede_168,
            SupportedAlgorithm.DESede_112
    };

    private boolean autoCreateSecretKey = true;

    private boolean autoEncryptNonEncryptedValues = true;

    /**
     * Return the SecretFile location which is needed to decrypt and encrypt your property-values.
     * <p>
     * Default is $HOME/.secret/securedProperties.key
     */
    public File getSecretFile() {
        if (secretFile == null) {
            final String secretFilePath = SystemUtils.USER_HOME + "/.secret/securedProperties.key";
            LOG.debug("No secretFilePath configured. Use default location: {}", secretFilePath);
            secretFile = new File(secretFilePath);
        }
        return secretFile;
    }

    public int getSaltLength() {
        return saltLength;
    }

    public boolean isAutoCreateSecretKey() {
        return autoCreateSecretKey;
    }

    public boolean isAutoEncryptNonEncryptedValues() {
        return autoEncryptNonEncryptedValues;
    }

    public Algorithm[] getAllowedAlgorithm() {
        return allowedAlgorithm;
    }

    /**
     * Similar to Spring-Boot, Externalize your configuration so that you can work with the same application code in different environments.
     * <p>
     * <ol>
     * <li>Application property Files './application.properties'. If the File doesn't exist, it will be ignored.</li>
     * <li>Application property Files './config/application.properties'. If the File doesn't exist, it will be ignored.</li>
     * <li>OS environment variables.</li>
     * <li>Java System properties (System.getProperties()).</li>
     * </ol>
     * The last one has the highest priority and will overwrite properties before.
     * <p>
     * The Properties which can be configured can be found in {@link net.brabenetz.lib.securedproperties.config.ConfigKey}.<br>
     * The default prefix is "SECURED_PROPERTIES" and the keys must be configured formatted as:
     * <ul>
     * <li><b>UPPER_CASE:</b> Like "SECURED_PROPERTIES_SECRET_FILE" is used for <b>OS environment variables</b>.</li>
     * <li><b>kebab-case:</b> Like "secured-properties.secret-file" is used for <b>System-Properties</b> and <b>Property-Files</b></li>
     * </ul>
     *
     * @see "https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html"
     * @return this for fluent style.
     */
    public SecuredPropertiesConfig initDefault() {
        return init(ConfigInitializers.propertyFile(new File("./application.properties")),
                ConfigInitializers.propertyFile(new File("./config/application.properties")),
                ConfigInitializers.envProperties(),
                ConfigInitializers.systemProperties());
    }

    /**
     * The generic variant of {@link #initDefault()}. Just put your {@link ConfigInitializer}s into it in the order you want.
     * <p>
     * The last one has the highest priority and will overwrite properties before.
     *
     * @param configInitializers your {@link ConfigInitializer}s. See {@link ConfigInitializers}.
     * @return this for fluent style.
     */
    public SecuredPropertiesConfig init(final ConfigInitializer... configInitializers) {
        Arrays.asList(configInitializers).forEach(configInit -> configInit.init(this));
        return this;
    }

    /**
     * Overwrite the default location "%user_home%/.secret/securedProperties.key" for the secret key to encrypt and decrypt values.
     *
     * @param newSecretFile the secret File
     * @return this for fluent style.
     */
    @Override
    public SecuredPropertiesConfig withSecretFile(final File newSecretFile) {
        secretFile = newSecretFile;
        return this;
    }

    @Override
    public SecuredPropertiesConfig withSaltLength(final int newSaltLength) {
        saltLength = newSaltLength;
        return this;
    }

    @Override
    public SecuredPropertiesConfig withAllowedAlgorithm(final Algorithm... newAllowedAlgorithm) {
        allowedAlgorithm = newAllowedAlgorithm;
        return this;
    }

    public SecuredPropertiesConfig addAllowedAlgorithm(final Algorithm... addedAllowedAlgorithm) {
        allowedAlgorithm = ArrayUtils.addAll(allowedAlgorithm, addedAllowedAlgorithm);
        return this;
    }

    @Override
    public SecuredPropertiesConfig withAutoCreateSecretKey(final boolean autoCreate) {
        autoCreateSecretKey = autoCreate;
        return this;
    }

    @Override
    public SecuredPropertiesConfig withAutoEncryptNonEncryptedValues(final boolean autoEncrypt) {
        autoEncryptNonEncryptedValues = autoEncrypt;
        return this;
    }

}
