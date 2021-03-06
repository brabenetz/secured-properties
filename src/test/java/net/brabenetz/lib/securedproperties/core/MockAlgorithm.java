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
 * Mock implementation of {@link Algorithm} for testing exception handling for invalid Algorithms.
 */
public class MockAlgorithm implements Algorithm {
    private final String key;
    private final int size;

    /** @see MockAlgorithm */
    public MockAlgorithm(final String key, final int size) {
        super();
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

    @Override
    public String toString() {
        return String.format("%s_%s", getKey(), getSize());
    }

}