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

import com.github.fge.lambdas.Throwing;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

import javax.crypto.SecretKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility to read (and write if not already exists) a {@link SecretContainer} object from FileSystem.
 * 
 * @see #getSecretContainer(File, boolean, Algorithm...)
 */
public final class SecretContainerStore {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SecretContainerStore.class);

    private SecretContainerStore() {
        super();
    }

    /**
     * Read the {@link SecretContainer} object from the given Secret File. Can also auto-create the secret File if it doesn't exist.
     * 
     * @param secretContainerFile
     *        The location where the secret file should be placed.
     * @param autoCreateSecretKey
     *        flag to auto create the secret File if it doesn't already exists. If false, and the secret File is missing an Exception will be thrown.
     * @param allowedAlgorithm
     *        A list of allowed {@link Algorithm}s in case the secret File must be generated. The first by the Java-VM supported {@link Algorithm} will be used
     *        to create the secret File
     * @return the {@link SecretContainer}
     */
    public static SecretContainer getSecretContainer(final File secretContainerFile, final boolean autoCreateSecretKey, final Algorithm... allowedAlgorithm) {

        final SecretContainer secretContainer;
        if (secretContainerFile.exists()) {
            secretContainer = read(secretContainerFile, allowedAlgorithm);
        } else if (autoCreateSecretKey) {
            Algorithm algorithm = Encryption.getFirstSupportedAlgorithm(allowedAlgorithm);

            SecretKey secretKey = Encryption.createKey(algorithm);
            secretContainer = new SecretContainer(algorithm, secretKey);
            write(secretContainer, secretContainerFile);
        } else {
            throw new IllegalArgumentException(String.format("Secret file '%s' doesn't exist, and auto create is off.", secretContainerFile.getAbsolutePath()));
        }
        return secretContainer;
    }

    private static void write(final SecretContainer secretContainer, final File file) {
        List<String> lines = new ArrayList<>();
        lines.add(secretContainer.getAlgorithm().toString());
        lines.add(Encryption.toBase64String(secretContainer.getSecretKey()));
        Throwing.runnable(() -> FileUtils.writeLines(file, "utf-8", lines)).run();
    }

    // SuppressWarnings "PMD.PreserveStackTrace": Stacktrace will be logged on debug level if really required.
    @SuppressWarnings("PMD.PreserveStackTrace")
    private static SecretContainer read(final File file, final Algorithm... allowedAlgorithms) {
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
            String errorMessage = String.format("The secret key could not be read from File '%s'. %s", file.getAbsolutePath(), e.getMessage());
            LOG.debug("{}", errorMessage, e);
            throw new IllegalArgumentException(errorMessage);
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
