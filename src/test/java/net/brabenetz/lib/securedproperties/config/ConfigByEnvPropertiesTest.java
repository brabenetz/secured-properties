/*-
 * #%L
 * Secured Properties
 * ===============================================================
 * Copyright (C) 2016 - 2019 Brabenetz Harald, Austria
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
package net.brabenetz.lib.securedproperties.config;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ConfigByEnvPropertiesTest {

    @Test
    public void testGetValue() throws Exception {
        // Value is set for maven-surefire-plugin in pom.xml: TEST_SECURED_PROPERTIES_SECRET_FILE = Test-value-for-UnitTest
        ConfigByEnvProperties configInitializer = new ConfigByEnvProperties("TEST_SECURED_PROPERTIES");

        assertThat(configInitializer.getValue(ConfigKey.SECRET_FILE), is(equalTo("Test-value-for-UnitTest")));
    }

    @Test
    public void testGetValu_NOT_EXISTING() throws Exception {
        // value cannot be set on development-computer where tests should run.
        ConfigByEnvProperties configInitializer = new ConfigByEnvProperties();

        assertThat(configInitializer.getValue(ConfigKey.AUTO_CREATE_SECRET_KEY), is(nullValue()));
    }

}
