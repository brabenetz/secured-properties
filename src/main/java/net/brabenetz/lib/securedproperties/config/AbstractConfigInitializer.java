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

import net.brabenetz.lib.securedproperties.core.Algorithm;
import net.brabenetz.lib.securedproperties.core.SupportedAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Parent helper class to easier implement a custom ConfigInitializer. Only {@link #getValue(ConfigKey)} must be implemented.
 */
public abstract class AbstractConfigInitializer implements ConfigInitializer {

    private static final Map<ConfigKey, BiConsumer<Config, String>> CONFIG_KEY_TO_INIT_FUNCTION_MAPPING = initConfigMappings();

    private final Function<ConfigKey, String> keyFactory;

    protected AbstractConfigInitializer() {
        this((final ConfigKey key) -> ConfigKey.DEFAULT_PREFIX_KEBAB_CASE + '.' + key.getKebabCase());
    }

    protected AbstractConfigInitializer(final Function<ConfigKey, String> keyFactory) {
        this.keyFactory = keyFactory;
    }

    /**
     * Small convenience Constructor for concrete implementation which can have an optional config key-prefix.
     */
    protected AbstractConfigInitializer(final String configKeyPrefix,
            final Function<ConfigKey, String> keyFactoryWithoutPrefix,
            final Function<ConfigKey, String> keyFactoryWithPrefix) {
        if (StringUtils.isEmpty(configKeyPrefix)) {
            keyFactory = keyFactoryWithoutPrefix;
        } else {
            keyFactory = keyFactoryWithPrefix;
        }
    }

    protected Function<ConfigKey, String> getKeyFactory() {
        return keyFactory;
    }

    @Override
    public void init(final Config config) {
        for (final ConfigKey configKey : ConfigKey.values()) {
            final String value = getValue(configKey);
            if (value != null) {
                CONFIG_KEY_TO_INIT_FUNCTION_MAPPING.get(configKey).accept(config, value);
            }
        }
    }

    private static Map<ConfigKey, BiConsumer<Config, String>> initConfigMappings() {
        final Map<ConfigKey, BiConsumer<Config, String>> mapping = new EnumMap<>(ConfigKey.class);
        mapping.put(ConfigKey.SECRET_FILE, AbstractConfigInitializer::initSecretFilePath);
        mapping.put(ConfigKey.SALT_LENGTH, AbstractConfigInitializer::initSaltLength);
        mapping.put(ConfigKey.ALLOWED_ALGORITHM, AbstractConfigInitializer::initAllowedAlgorithm);
        mapping.put(ConfigKey.AUTO_CREATE_SECRET_KEY, AbstractConfigInitializer::initAutoCreateSecretKey);
        mapping.put(ConfigKey.AUTO_ENCRYPT_NON_ENCRYPTED_VALUES, AbstractConfigInitializer::initAutoEncryptNonEncryptedValues);
        return mapping;

    }

    protected static void initSecretFilePath(final Config config, final String value) {
        config.withSecretFile(new File(value));
    }

    protected static void initSaltLength(final Config config, final String value) {
        config.withSaltLength(Integer.parseInt(value));
    }

    protected static void initAllowedAlgorithm(final Config config, final String value) {
        config.withAllowedAlgorithm(Arrays.stream(value.split("[, ]+")).map(SupportedAlgorithm::valueOf).toArray(Algorithm[]::new));
    }

    protected static void initAutoCreateSecretKey(final Config config, final String value) {
        config.withAutoCreateSecretKey(Boolean.valueOf(value));
    }

    protected static void initAutoEncryptNonEncryptedValues(final Config config, final String value) {
        config.withAutoEncryptNonEncryptedValues(Boolean.valueOf(value));
    }

    /**
     * @param key The {@link ConfigKey} to search the value for.
     * @return the Value as simple string.
     */
    protected abstract String getValue(ConfigKey key);
}
