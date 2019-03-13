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

import com.iomolecule.config.ConfigurationException;
import com.iomolecule.config.ConfigurationSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModuleUtils {


    public static <T> T getConfig(String configPath,Class<T> configClass, ConfigurationSource configurationSource,T defaultConfig){
        T finalConfig = defaultConfig;

        if(configurationSource.isValid(configPath)){
            try {
                finalConfig = configurationSource.get(configPath, configClass);
            } catch (ConfigurationException e) {
                log.info("Unable to load config for path {}, so defaulting to {}",configPath,defaultConfig);
            }
        }else{
            log.info("Unable to load config for path {}, so defaulting to {}",configPath,defaultConfig);
        }

        return finalConfig;

    }
}
