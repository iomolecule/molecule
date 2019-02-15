/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iomolecule.util;

import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.config.InputStreamConfigurationSource;
import com.iomolecule.config.ConfigurationException;
import org.junit.Test;

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
