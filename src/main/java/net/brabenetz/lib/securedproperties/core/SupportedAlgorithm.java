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
package net.brabenetz.lib.securedproperties.core;

/**
 * Some default implementations of {@link Algorithm}.
 */
public enum SupportedAlgorithm implements Algorithm {
    /** AES 256 bit encoding. By default, Java only supports 128 bit of AES. */
    AES_256("AES", 256),
    /** AES 192 bit encoding. By default, Java only supports 128 bit of AES. */
    AES_192("AES", 192),
    /** AES 128 bit encoding. */
    AES_128("AES", 128),
    /** DESede 168 bit encoding. Also known as TripleDES. */
    DESede_168("DESede", 168),
    /** DESede 112 bit encoding. Also known as TripleDES. */
    DESede_112("DESede", 112);

    private final String key;
    private final int size;

    SupportedAlgorithm(final String key, final int size) {
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
