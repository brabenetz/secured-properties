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

import net.brabenetz.lib.securedproperties.utils.SecuredPropertiesUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.Properties;

/**
 * {@link ConfigInitializer} implementation to read from {@link Properties} Files.
 * <p>
 * The property-Prefix is "secured-properties".<br>
 * The Keys must be kebab-case like 'secured-properties.secret-file'.
 */
public class ConfigByPropertyFile extends AbstractConfigInitializer {

    private final Properties properties;

    public ConfigByPropertyFile(final File propertyFile) {
        this(ConfigKey.DEFAULT_PREFIX_KEBAB_CASE, propertyFile);
    }

    /**
     * @param configKeyPrefix an optional key prefix (default is 'secured-properties').
     * @param propertyFile    required, but the {@link Properties} File itself is the optional.<br>
     *                        If the File doesn't exist an empty Properties Object will be used to prevent errors.
     */
    public ConfigByPropertyFile(final String configKeyPrefix, final File propertyFile) {
        super(configKeyPrefix, ConfigKey::getKebabCase, (final ConfigKey key) -> configKeyPrefix + "." + key.getKebabCase());
        Validate.notNull(propertyFile, "The Propety-File is required");
        if (propertyFile.exists()) {
            properties = SecuredPropertiesUtils.readProperties(propertyFile);
        } else {
            properties = new Properties();
        }
    }

    @Override
    protected String getValue(final ConfigKey key) {
        return properties.getProperty(getKeyFactory().apply(key));
    }

}
