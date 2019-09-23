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

import net.brabenetz.lib.securedproperties.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

public class ConfigInitializersTest {

    @Test
    public void testUtilityPattern() {
        // this test is more for test-coverage than logic :D
        Assert.assertTrue(TestUtils.isDefaultConstructorHidden(ConfigInitializers.class));
    }
}
