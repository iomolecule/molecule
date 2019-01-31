package org.molecule.util;

import org.junit.Test;
import org.molecule.config.ConfigurationException;
import org.molecule.config.ConfigurationSource;
import org.molecule.config.InputStreamConfigurationSource;

public class ConfigurationSourceTests {

    @Test
    public void testInputStreamConfigurationSourceSimple() throws ConfigurationException {

        ConfigurationSource testConfigSource = new InputStreamConfigurationSource(false,true,
                getClass().getResourceAsStream("/test-inputstream-source.json"),
                getClass().getResourceAsStream("/test-inputstream-source-override.json"));

        ParentConfig simpleConfig = testConfigSource.get("/parentConfig", ParentConfig.class);

        System.out.println("SimpleConfig "+simpleConfig);

        String testKeyVal = testConfigSource.get("/parentConfig/simpleConfig/testKey", String.class);
        System.out.println("TestKeyVal "+testKeyVal);

        Boolean dblValue = testConfigSource.get("/simpleConfig/someValue", Boolean.class);
        System.out.println("TestKeyVal "+dblValue);

        Boolean aBoolean = testConfigSource.get("/invalidValue", Boolean.class, Boolean.FALSE);
        System.out.println("InvalidValue "+aBoolean);

        String treasureValue = testConfigSource.get("/complexJSON1/level0/level1/level2/level3/treasure", String.class);

        System.out.println("TreasureValue "+treasureValue);

    }

}
