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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ConfigBySystemPropertiesTest {

    @Before
    public void before() {
        System.setProperty("secured-properties.secret-file", "Test-value-for-Sys-Property-UnitTest");
    }

    @After
    public void after() {
        System.clearProperty("secured-properties.secret-file");
    }

    @Test
    public void testGetValue() throws Exception {
        ConfigBySystemProperties configInitializer = new ConfigBySystemProperties();

        assertThat(configInitializer.getValue(ConfigKey.SECRET_FILE), is(equalTo("Test-value-for-Sys-Property-UnitTest")));
        assertThat(configInitializer.getValue(ConfigKey.AUTO_ENCRYPT_NON_ENCRYPTED_VALUES), is(nullValue()));
    }

}
