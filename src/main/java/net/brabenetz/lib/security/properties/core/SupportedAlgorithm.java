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

public enum SupportedAlgorithm implements Algorithm {
    AES_256("AES", 256), // By default, Java only supports AES 128
    AES_192("AES", 192), // By default, Java only supports AES 128
    AES_128("AES", 128),
    DESede_168("DESede", 168),
    DESede_112("DESede", 112);

    private final String key;
    private final int size;

    private SupportedAlgorithm(final String key, final int size) {
        this.key = key;
        this.size = size;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public int getSize() {
        return this.size;
    }
}
