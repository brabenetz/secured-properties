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
