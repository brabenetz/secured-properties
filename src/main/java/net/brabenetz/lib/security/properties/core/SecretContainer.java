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
package net.brabenetz.lib.security.properties.core;

import javax.crypto.SecretKey;

public class SecretContainer {

    private final Algorithm algorithm;
    private final SecretKey secretKey;

    public SecretContainer(final Algorithm algorithm, final SecretKey secretKey) {
        super();
        this.algorithm = algorithm;
        this.secretKey = secretKey;
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

}