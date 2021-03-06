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

import lombok.extern.slf4j.Slf4j;
import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.system.ExceptionHandler;

@Slf4j
class DefaultExceptionHandler implements ExceptionHandler {


    private ConfigurationSource configurationSource;

    DefaultExceptionHandler(ConfigurationSource messageConfigurationSource){
        this.configurationSource = messageConfigurationSource;
    }

    @Override
    public void handle(Throwable throwable) {

        String message = throwable.getMessage();
        log.debug("Error Message {}",message);
        log.debug("Handling exception {} with message config source {}",throwable,configurationSource);

    }
}
