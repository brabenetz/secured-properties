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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

import com.github.fge.lambdas.Throwing;

public final class SecretContainerStore {

    public static SecretContainer getSecretContainer(final File secretContainerFile, final boolean autoCreateSecretKey, final Algorithm... allowedAlgorithm) {

        final SecretContainer secretContainer;
        if (secretContainerFile.exists()) {
            secretContainer = SecretContainerStore.read(secretContainerFile, allowedAlgorithm);
        } else if (autoCreateSecretKey) {
            Algorithm algorithm = Encryption.getFirstSupportedAlgorithm(allowedAlgorithm);

            SecretKey secretKey = Encryption.createKey(algorithm);
            secretContainer = new SecretContainer(algorithm, secretKey);
            SecretContainerStore.write(secretContainer, secretContainerFile);
        } else {
            throw new IllegalArgumentException(String.format("Secret file '%s' doesn't exist, and auto create is off.", secretContainerFile.getAbsolutePath()));
        }
        return secretContainer;
    }

    private static void write(final SecretContainer secretContainer, final File file) {
        List<String> lines = new ArrayList<>();
        lines.add(secretContainer.getAlgorithm().toString());
        lines.add(Encryption.toString(secretContainer.getSecretKey()));
        Throwing.runnable(() -> FileUtils.writeLines(file, "utf-8", lines)).run();
    }

    private static SecretContainer read(final File file, final Algorithm[] allowedAlgorithms) {
        try {
            List<String> readLines = Throwing.supplier(() -> FileUtils.readLines(file, "utf-8")).get();
            Validate.isTrue(readLines.size() >= 2, "secrete File must have at least two lines");
            String algorithmStr = readLines.get(0);
            Algorithm algorithm = parseAlgorithm(allowedAlgorithms, algorithmStr);
            Validate.notNull(algorithm, "Unable to parse algorithm '%s'. Allowed algorithms are: %s",
                algorithmStr, Arrays.asList(allowedAlgorithms));
            SecretKey secretKey = Encryption.readSecretKey(algorithm, readLines.get(1));
            return new SecretContainer(algorithm, secretKey);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("The secret key could not be read from File '%s'. %s", file.getAbsolutePath(), e.getMessage()));
        }
    }

    private static Algorithm parseAlgorithm(final Algorithm[] allowedAlgorithms, final String algorithmStr) {
        for (Algorithm allowedAlgorithm : allowedAlgorithms) {
            if (algorithmStr.equals(allowedAlgorithm.toString())) {
                return allowedAlgorithm;
            }
        }
        return null;
    }
}
