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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class SecuredPropertiesUtils {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SecuredPropertiesUtils.class);

    public static Properties readProperties(final File propertyFile) {
        final Properties properties = new Properties();
        if (propertyFile == null) {
            return properties;
        }
        byte[] propertyFileContent = Throwing.supplier(() -> FileUtils.readFileToByteArray(propertyFile)).get();
        Throwing.runnable(() -> properties.load(new ByteArrayInputStream(propertyFileContent))).run();
        return properties;
    }

    public static File getSecretFile(final File defaultSecretFile, final String secretFilePropertyKey, final Properties properties) {
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

    public static void replaceSecretValue(final File propertyFile, final String key, final String encryptedPassword) {
        List<String> lines = Throwing.supplier(() -> FileUtils.readLines(propertyFile, StandardCharsets.ISO_8859_1.name())).get();
        List<String> newLines = lines.stream().map((line) -> ValueReplacementUtils.replaceValue(line, key, encryptedPassword))
            .collect(Collectors.toList());
        Throwing.runnable(() -> FileUtils.writeLines(propertyFile, StandardCharsets.ISO_8859_1.name(), newLines)).run();
    }

}
