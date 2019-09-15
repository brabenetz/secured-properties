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

import net.brabenetz.lib.securedproperties.core.SupportedAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AbstractConfigInitializerTest {
    @Test
    public void testInit_withExampleVAlues_shouldSetRightConfigs() throws Exception {
        AbstractConfigInitializer configInitializer = new AbstractConfigInitializer() {
            @Override
            protected String getValue(ConfigKey key) {
                switch (key) {
                    case SECRET_FILE:
                        return "./src/test/data/secretFileExample.key";
                    case SALT_LENGTH:
                        return "15";
                    case ALLOWED_ALGORITHM:
                        return "AES_128,DESede_168";
                    case AUTO_CREATE_SECRET_KEY:
                        return "false";
                    default:
                        throw new RuntimeException("Invalid Key: " + key);
                }
            }
        };

        Config config = Mockito.mock(Config.class);
        configInitializer.init(config);
        Mockito.verify(config).withSecretFile(new File("./src/test/data/secretFileExample.key"));
        Mockito.verify(config).withSaltLength(15);
        Mockito.verify(config).withAllowedAlgorithm(SupportedAlgorithm.AES_128, SupportedAlgorithm.DESede_168);
        Mockito.verify(config).withAutoCreateSecretKey(false);

        assertThat(configInitializer.getKeyFactory().apply(ConfigKey.SECRET_FILE),
                is(equalTo("secured-properties.secret-file")));
        assertThat(configInitializer.getKeyFactory().apply(ConfigKey.AUTO_CREATE_SECRET_KEY),
                is(equalTo("secured-properties.auto-create-secret-key")));
    }

    @Test
    public void testInit_withoutValues_shouldNotSetAnyConfigs() throws Exception {
        AbstractConfigInitializer configInitializer = new AbstractConfigInitializer(
                null, (key) -> key.getUpperCase(), null) {
            @Override
            protected String getValue(ConfigKey key) {
                return null;
            }
        };

        Config config = Mockito.mock(Config.class);
        configInitializer.init(config);
        Mockito.verify(config, Mockito.never()).withSecretFile(ArgumentMatchers.any(File.class));
        Mockito.verify(config, Mockito.never()).withSaltLength(ArgumentMatchers.anyInt());
        Mockito.verify(config, Mockito.never()).withAllowedAlgorithm(ArgumentMatchers.any(SupportedAlgorithm[].class));
        Mockito.verify(config, Mockito.never()).withAutoCreateSecretKey(ArgumentMatchers.anyBoolean());

        assertThat(configInitializer.getKeyFactory().apply(ConfigKey.SECRET_FILE),
                is(equalTo("SECRET_FILE")));
        assertThat(configInitializer.getKeyFactory().apply(ConfigKey.AUTO_CREATE_SECRET_KEY),
                is(equalTo("AUTO_CREATE_SECRET_KEY")));
    }
}
