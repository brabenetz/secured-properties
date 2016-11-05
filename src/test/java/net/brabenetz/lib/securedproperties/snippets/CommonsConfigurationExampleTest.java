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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.junit.Test;

import net.brabenetz.lib.securedproperties.SecuredProperties;
import net.brabenetz.lib.securedproperties.SecuredPropertiesConfig;

public class CommonsConfigurationExampleTest {

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

        // initialization - create CompositeConfiguration which first reads SystemProperties and then the PropertiesFile.
        SystemConfiguration systemConfiguration = new SystemConfiguration();

        PropertiesConfiguration propertiesConfiguration =
            new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().properties().setFile(propertiesFile))
            .getConfiguration();

        CompositeConfiguration commonConfig = new CompositeConfiguration();
        commonConfig.addConfiguration(systemConfiguration);
        commonConfig.addConfiguration(propertiesConfiguration);

        // somewhere in your application (e.g. with dependency injection of commonConfig):
        assertThat(commonConfig.getString("myTitle"), is("My Test")); // some value from PropertyFile
        assertThat(commonConfig.getString("mySecretPassword"), is("test")); // decrypted Password from SystemProperties.
        // END SNIPPET: configExample

    }
}
