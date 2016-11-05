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
package net.brabenetz.lib.securedproperties.snippets;

import net.brabenetz.lib.securedproperties.SecuredProperties;
import net.brabenetz.lib.securedproperties.SecuredPropertiesConfig;
import org.junit.Test;
import org.settings4j.Settings4j;
import org.settings4j.connector.PropertyFileConnector;
import org.settings4j.connector.SystemPropertyConnector;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.settings4j.ConnectorPositions.afterLast;

/**
 * Snippet for src/site/markdown/exampleSettings4j.md with compile validation.
 */
public class Settings4jExampleTest {

    @Test
    public void testSettings4jConfiguration() throws Exception {

        // START SNIPPET: configExample
        // initialization
        File secretKey = new File("src/test/data/secretFileExample.key");
        File propertiesFile = new File("src/test/data/TestProperties-Valid.properties");

        // initialization - get decrypted value
        String myPassword = SecuredProperties.getSecretValue(
            new SecuredPropertiesConfig().withDefaultSecretFile(secretKey),
            propertiesFile,
            "mySecretPassword");
        System.setProperty("mySecretPassword", myPassword);

        // initialization - add custom Settings4j PropertyFileConnector after existing SystemPropertyConnector
        PropertyFileConnector myConnector = new PropertyFileConnector();
        myConnector.setName("myCustomConfig");
        myConnector.setPropertyFromPath(propertiesFile.toURI().toString());
        Settings4j.getSettingsRepository().getSettings().addConnector(myConnector, afterLast(SystemPropertyConnector.class));

        // somewhere in your application:
        assertThat(Settings4j.getString("myTitle"), is("My Test")); // some value from PropertyFile
        assertThat(Settings4j.getString("mySecretPassword"), is("test")); // decrypted Password from SystemProperties.
        // END SNIPPET: configExample

    }
}
