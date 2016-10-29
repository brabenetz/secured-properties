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

import org.junit.Assert;
import org.junit.Test;

import net.brabenetz.lib.securedproperties.utils.ValueReplacementUtils;

public class ValueReplacementUtilsTest {

    @Test
    public void testReplaceValue() throws Exception {

        // update Property
        Assert.assertEquals("test=newTest", ValueReplacementUtils.replaceValue("test=test", "test", "newTest"));
        // unchanged Property
        Assert.assertEquals("testForSomethingOther=test", ValueReplacementUtils.replaceValue("testForSomethingOther=test", "test", "newTest"));
        Assert.assertEquals("otherKey=test", ValueReplacementUtils.replaceValue("otherKey=test", "test", "newTest"));
        // property with whitespaces
        Assert.assertEquals("test = newTest", ValueReplacementUtils.replaceValue("test = test", "test", "newTest"));
        Assert.assertEquals("test  \t\t =\t newTest", ValueReplacementUtils.replaceValue("test  \t\t =\t test", "test", "newTest"));
    }

}