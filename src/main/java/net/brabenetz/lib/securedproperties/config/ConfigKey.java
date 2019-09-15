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

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The Keys which can be external configured to set values in {@link Config}.
 */
public enum ConfigKey {

    /** for setting the value {@link Config#withSecretFile(java.io.File)}. */
    SECRET_FILE,
    /** for setting the value {@link Config#withSaltLength(int)}. */
    SALT_LENGTH,
    /** for setting the value {@link Config#withAllowedAlgorithm(net.brabenetz.lib.securedproperties.core.Algorithm...)}. */
    ALLOWED_ALGORITHM,
    /** for setting the value {@link Config#withAutoCreateSecretKey(boolean)}. */
    AUTO_CREATE_SECRET_KEY,
    /** for setting the value {@link Config#withAutoEncryptNonEncryptedValues(boolean)}. */
    AUTO_ENCRYPT_NON_ENCRYPTED_VALUES;

    /** default prefix for UPPER_CASE keys. */
    public static final String DEFAULT_PREFIX_UPPER_CASE = "SECURED_PROPERTIES";
    /** default prefix for kebab-case keys. */
    public static final String DEFAULT_PREFIX_KEBAB_CASE = getKebabCase(DEFAULT_PREFIX_UPPER_CASE);

    private final String kebabCase;

    ConfigKey() {
        kebabCase = getKebabCase(getUpperCase());
    }

    public String getUpperCase() {
        return name();
    }

    public String getKebabCase() {
        return kebabCase;
    }

    private static String getKebabCase(final String upperCase) {
        return Arrays.stream(upperCase.split("_"))
                .map(String::toLowerCase)
                .collect(Collectors.joining("-"));
    }
}
