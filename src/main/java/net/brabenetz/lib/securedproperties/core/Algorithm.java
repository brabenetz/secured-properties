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
 * The Algorithm interface for SecuredProperties.
 */
public interface Algorithm {

    /**
     * The Algorithm key, see {@link javax.crypto.Cipher#getInstance(String)}.
     */
    String getKey();

    /**
     * The Algorithm keysize of the initial secret key, see
     * {@link javax.crypto.KeyGenerator#init(int)}.
     */
    int getSize();

}