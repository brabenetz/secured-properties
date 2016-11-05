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
package net.brabenetz.lib.securedproperties;

import net.brabenetz.lib.securedproperties.core.Algorithm;
import net.brabenetz.lib.securedproperties.core.SupportedAlgorithm;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;

public class SecuredPropertiesConfig {

    private File defaultSecretFile;
    private String secretFilePropertyKey;
    private Algorithm[] allowedAlgorithm = new Algorithm[] {
            SupportedAlgorithm.AES_256,
            SupportedAlgorithm.AES_192,
            SupportedAlgorithm.AES_128,
            SupportedAlgorithm.DESede_168,
            SupportedAlgorithm.DESede_112
    };

    private boolean autoCreateSecretKey = true;

    private boolean autoEncryptNonEncryptedValues = true;

    public File getDefaultSecretFile() {
        return this.defaultSecretFile;
    }

    public String getSecretFilePropertyKey() {
        return this.secretFilePropertyKey;
    }

    public boolean isAutoCreateSecretKey() {
        return this.autoCreateSecretKey;
    }

    public boolean isAutoEncryptNonEncryptedValues() {
        return this.autoEncryptNonEncryptedValues;
    }

    public Algorithm[] getAllowedAlgorithm() {
        return this.allowedAlgorithm;
    }

    public SecuredPropertiesConfig withDefaultSecretFile(final File newSecretFile) {
        this.defaultSecretFile = newSecretFile;
        return this;
    }

    public SecuredPropertiesConfig withSecretFilePropertyKey(final String newSecretFilePropertyKey) {
        this.secretFilePropertyKey = newSecretFilePropertyKey;
        return this;
    }

    public SecuredPropertiesConfig withAllowedAlgorithm(final Algorithm... newAllowedAlgorithm) {
        this.allowedAlgorithm = newAllowedAlgorithm;
        return this;
    }

    public SecuredPropertiesConfig addAllowedAlgorithm(final Algorithm... addedAllowedAlgorithm) {
        this.allowedAlgorithm = ArrayUtils.addAll(this.allowedAlgorithm, addedAllowedAlgorithm);
        return this;
    }

    public SecuredPropertiesConfig disableAutoCreateSecretKey() {
        this.autoCreateSecretKey = false;
        return this;
    }

    public SecuredPropertiesConfig disableAutoEncryptNonEncryptedValues() {
        this.autoEncryptNonEncryptedValues = false;
        return this;
    }

}
