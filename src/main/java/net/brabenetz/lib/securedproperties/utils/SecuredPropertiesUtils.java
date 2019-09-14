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
