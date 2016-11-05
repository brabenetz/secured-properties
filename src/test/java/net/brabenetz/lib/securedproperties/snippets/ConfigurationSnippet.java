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

import net.brabenetz.lib.securedproperties.SecuredPropertiesConfig;
import net.brabenetz.lib.securedproperties.core.SupportedAlgorithm;

import java.io.File;

/**
 * Snippet for src/site/markdown/configuration.md with compile validation.
 */
public class ConfigurationSnippet {

    @SuppressWarnings("unused")
    // START SNIPPET: configExample
    private SecuredPropertiesConfig config = new SecuredPropertiesConfig()
        .withSecretFilePropertyKey("secretKeyPath") // [1]
        .withDefaultSecretFile(new File("./mySecret.key")) // [2]
        .withSaltLength(0) // [3]
        .withAllowedAlgorithm(SupportedAlgorithm.AES_256) // [4]
        .addAllowedAlgorithm(SupportedAlgorithm.DESede_168) // [5]
        .disableAutoCreateSecretKey() // [6]
        .disableAutoEncryptNonEncryptedValues(); // [7]
    // END SNIPPET: configExample
}
