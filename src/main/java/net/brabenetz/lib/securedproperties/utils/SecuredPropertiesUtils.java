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
package net.brabenetz.lib.securedproperties.utils;

import com.github.fge.lambdas.Throwing;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Some Utilities for {@link net.brabenetz.lib.securedproperties.SecuredProperties}.
 */
public final class SecuredPropertiesUtils {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SecuredPropertiesUtils.class);

    private SecuredPropertiesUtils() {
        super();
    }

    /**
     * Loads a {@link Properties} object from a given Property {@link File} and transform checked
     * Exception into RuntimeExceptions.
     * 
     * @param propertyFile
     *        the property File
     * @return the {@link Properties} object loaded by the propertyFile
     */
    public static Properties readProperties(final File propertyFile) {
        final Properties properties = new Properties();
        if (propertyFile == null) {
            return properties;
        }
        byte[] propertyFileContent = Throwing.supplier(() -> FileUtils.readFileToByteArray(propertyFile)).get();
        Throwing.runnable(() -> properties.load(new ByteArrayInputStream(propertyFileContent))).run();
        return properties;
    }

    /**
     * Determine the location of the secret Key file by the given parameters.
     * <p>
     * logic checks the following order:
     * <ol>
     * <li>try to read the location from the Properties-File by the given
     * secretFilePropertyKey.</li>
     * <li>if no value found. but a defaultSecretFile is given, then the defaultSecretFile will be
     * returned.</li>
     * <li>if custom defaultSecretFile is given, then the default location
     * "%USER_HOME%/.secret/securedProperties.key" will be returned.</li>
     * </ol>
     * 
     * @param secretFilePropertyKey
     *        The Property Key to get the location of the secret File from the given properties
     *        file.
     * @param defaultSecretFile
     *        The optional overwritten default location if no value for the secretFilePropertyKey is
     *        placed.
     * @param properties
     *        the Properties to search for the value for secretFilePropertyKey.
     * @return
     */
    public static File getSecretFile(final String secretFilePropertyKey, final File defaultSecretFile, final Properties properties) {
        String secretFilePath = null;
        if (StringUtils.isNotEmpty(secretFilePropertyKey)) {
            secretFilePath = properties.getProperty(secretFilePropertyKey);
        }

        if (StringUtils.isEmpty(secretFilePath)) {
            if (defaultSecretFile != null) {
                return defaultSecretFile;
            }
            secretFilePath = SystemUtils.USER_HOME + "/.secret/securedProperties.key";
            LOG.debug("No secretFilePath configured. Use default location: {}", secretFilePath);
        }

        return new File(secretFilePath);
    }

    /**
     * Replaces the value for one key in the given Properties file and leaves all other lines unchanged.
     * 
     * @param propertyFile
     *        The property File with the given Key
     * @param newProperties
     *        The new Property, where left is the key, and right is the new value.
     */
    @SafeVarargs
    public static void replaceSecretValue(final File propertyFile, final Pair<String, String>... newProperties) {
        List<String> lines = Throwing.supplier(() -> FileUtils.readLines(propertyFile, StandardCharsets.ISO_8859_1.name())).get();
        List<String> newLines = lines.stream()
            .map((line) -> replaceValue(line, newProperties))
            .collect(Collectors.toList());
        Throwing.runnable(() -> FileUtils.writeLines(propertyFile, StandardCharsets.ISO_8859_1.name(), newLines)).run();
    }

    // SuppressWarnings "PMD.DefaultPackage": only used in UnitTest
    @SafeVarargs
    @SuppressWarnings("PMD.DefaultPackage")
    static String replaceValue(final String line, final Pair<String, String>... newProperties) {
        for (Pair<String, String> newProperty : newProperties) {
            String key = newProperty.getLeft();
            String newValue = newProperty.getRight();
            Pattern pattern = Pattern.compile("^" + Pattern.quote(key) + "(\\s*=\\s*).*$");
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String gr1 = matcher.group(1);
                return key + gr1 + newValue;
            }
        }
        return line;
    }

}
