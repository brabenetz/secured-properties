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

public enum ConfigKey {

    SECRET_FILE,
    SALT_LENGTH,
    ALLOWED_ALGORITHM,
    AUTO_CREATE_SECRET_KEY,
    AUTO_ENCRYPT_NON_ENCRYPTED_VALUES;

    public static final String DEFAULT_PREFIX_UPPER_CASE = "SECURED_PROPERTIES";
    public static final String DEFAULT_PREFIX_KEBAB_CASE = getKebabCase(DEFAULT_PREFIX_UPPER_CASE);

    final String kebabCase;

    private ConfigKey() {
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
