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
package net.brabenetz.lib.securedproperties;

import net.brabenetz.lib.securedproperties.core.SupportedAlgorithm;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class SecuredPropertiesConfigTest {

    @Test
    public void testWithAllowedAlgorithm() throws Exception {
        SecuredPropertiesConfig config = new SecuredPropertiesConfig().withAllowedAlgorithm(SupportedAlgorithm.AES_256, SupportedAlgorithm.DESede_168);
        assertThat(Arrays.asList(config.getAllowedAlgorithm()), contains(SupportedAlgorithm.AES_256, SupportedAlgorithm.DESede_168));
    }

    @Test
    public void testAddAllowedAlgorithm() throws Exception {
        SecuredPropertiesConfig config = new SecuredPropertiesConfig().withAllowedAlgorithm().addAllowedAlgorithm(SupportedAlgorithm.DESede_168);
        assertThat(Arrays.asList(config.getAllowedAlgorithm()), contains(SupportedAlgorithm.DESede_168));
    }

    @Test
    public void testWithAndAddAllowedAlgorithm() throws Exception {
        SecuredPropertiesConfig config = new SecuredPropertiesConfig().withAllowedAlgorithm(SupportedAlgorithm.AES_256)
            .addAllowedAlgorithm(SupportedAlgorithm.DESede_168);
        assertThat(Arrays.asList(config.getAllowedAlgorithm()), contains(SupportedAlgorithm.AES_256, SupportedAlgorithm.DESede_168));
    }

}

