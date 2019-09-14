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

import net.brabenetz.lib.securedproperties.test.TestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SecuredPropertiesUtilsTest {

    @Test
    public void testUtilityPattern() {
        // this test is more for test-coverage than logic :D
        Assert.assertTrue(TestUtils.isDefaultConstructorHidden(SecuredPropertiesUtils.class));
    }

    @Test
    public void testReadProperties_Success_HappyPath() throws Exception {
        File propertyFile = getValidPropertyFile();
        Properties props = SecuredPropertiesUtils.readProperties(propertyFile);
        assertThat(props.get("testKey"), is("testValue"));
    }

    @Test
    public void testReadProperties_Fail_InvalidPropertyFile() throws Exception {
        File propertyFile = new File("src/test/data/TestProperties-Invalid.properties");
        Exception exc = TestUtils.expectException(() -> SecuredPropertiesUtils.readProperties(propertyFile));
        assertThat(exc, is(notNullValue()));
        assertThat(exc.getMessage(), containsString("Malformed \\uxxxx encoding"));
    }

    @Test
    public void testReadProperties_Fail_NoPropertyFile() throws Exception {
        File propertyFile = new File("src/test/data/TestProperties-Doesnt-Exist.properties");
        Exception exc = TestUtils.expectException(() -> SecuredPropertiesUtils.readProperties(propertyFile));
        assertThat(exc, is(notNullValue()));
        assertThat(exc, is(instanceOf(RuntimeException.class)));
        assertThat(exc.getCause(), is(instanceOf(FileNotFoundException.class)));
        assertThat(exc.getMessage(), containsString("TestProperties-Doesnt-Exist.properties"));
    }

    @Test
    public void testReplaceSecretValue() throws Exception {
        // prepare Property-File
        File propertyFile = new File("./target/tests/test.property");
        Properties validProperty = new Properties();
        validProperty.put("myPassword", "test");
        try (FileOutputStream propertyOutputStream = FileUtils.openOutputStream(propertyFile)) {
            validProperty.store(propertyOutputStream, "My Test Property");
        }
        // run test
        SecuredPropertiesUtils.replaceSecretValue(propertyFile, Pair.of("myPassword", "{abc-xyz}"));

        // validate Result
        Properties props = SecuredPropertiesUtils.readProperties(propertyFile);
        assertThat(props.get("myPassword"), is("{abc-xyz}"));
    }

    @Test
    public void testReplaceValue() throws Exception {

        // update Property
        Assert.assertEquals("test=newTest", SecuredPropertiesUtils.replaceValue("test=test", Pair.of("test", "newTest")));
        // unchanged Property
        Assert.assertEquals("testForSomethingOther=test", SecuredPropertiesUtils.replaceValue("testForSomethingOther=test", Pair.of("test", "newTest")));
        Assert.assertEquals("otherKey=test", SecuredPropertiesUtils.replaceValue("otherKey=test", Pair.of("test", "newTest")));
        // property with whitespaces should replace the value but leave the whitespaces unchanged.
        Assert.assertEquals("test = newTest", SecuredPropertiesUtils.replaceValue("test = test", Pair.of("test", "newTest")));
        Assert.assertEquals("test  \t\t =\t newTest", SecuredPropertiesUtils.replaceValue("test  \t\t =\t test", Pair.of("test", "newTest")));
        // property key with expression characters should be escaped
        Assert.assertEquals("testX = test", SecuredPropertiesUtils.replaceValue("testX = test", Pair.of("test.", "newTest")));
        Assert.assertEquals("test.\\(){} = newTest", SecuredPropertiesUtils.replaceValue("test.\\(){} = test", Pair.of("test.\\(){}", "newTest")));
    }

    private File getValidPropertyFile() {
        return new File("src/test/data/TestProperties-Valid.properties");
    }

}
