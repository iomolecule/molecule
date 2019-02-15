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

package com.iomolecule.mods.main;

import org.junit.Test;
import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.config.InputStreamConfigurationSource;
import com.iomolecule.system.ExceptionHandler;

public class ExceptionHandlerTests {

    @Test
    public void testSimpleExceptions(){

        ConfigurationSource source = new InputStreamConfigurationSource(false,false,
                getClass().getResourceAsStream(""));
        ExceptionHandler exceptionHandler = new DefaultExceptionHandler(source);

        int x = 0;
        int y = 40;

        try {
            int z = y / x;
        }catch(Exception e){
            exceptionHandler.handle(e);
        }


    }
}
