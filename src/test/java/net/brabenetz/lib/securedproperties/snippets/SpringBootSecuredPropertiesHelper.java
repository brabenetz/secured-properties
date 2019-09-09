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
package net.brabenetz.lib.securedproperties.snippets;

// START SNIPPET: configExample
import net.brabenetz.lib.securedproperties.SecuredProperties;
import net.brabenetz.lib.securedproperties.SecuredPropertiesConfig;
import net.brabenetz.lib.securedproperties.core.Encryption;

import java.io.File;

public class SpringBootSecuredPropertiesHelper {
    // Here you should look if you maybe need a custom config:
    private static final SecuredPropertiesConfig config = new SecuredPropertiesConfig();

    // the same property files which are supported by spring-boot:
    private static File[] propertyFiles = new File[] {new File("./config/application.properties"), new File("./application.properties")};

    public static void encryptProperties(String... keys) {
        SecuredProperties.getSecretValues(config, propertyFiles, keys);
    }

    public static String decrypt(String value) {
        if (Encryption.isEncryptedValue(value)) {
            return SecuredProperties.decrypt(config, value);
        }
        return value;
    }

}
// END SNIPPET: configExample
