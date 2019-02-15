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
import com.iomolecule.system.LifecycleException;
import com.iomolecule.system.LifecycleManager;
import com.iomolecule.system.annotations.LifecycleManagers;

import javax.inject.Inject;
import java.util.Map;

@Slf4j
class DefaultLifecycleManager implements LifecycleManager{

    private boolean started;

    private Map<String,LifecycleManager> lifecycleManagerMap;

    @Inject
    DefaultLifecycleManager(@LifecycleManagers Map<String,LifecycleManager> lifecycleManagerMap){
        this.lifecycleManagerMap = lifecycleManagerMap;
    }


    @Override
    public void start() throws LifecycleException {
        log.debug("Starting default lifecycle behaviour");


        //first start the main lifecycle manager so that base framework services are started in order
        getLifecycleManager("main").start();

        started = true;
    }

    @Override
    public void stop() {
        if(started){
            log.debug("Stopping default lifecycle behaviour");

            //first start the main lifecycle manager so that base framework services are started in order

            try {
                getLifecycleManager("main").stop();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }

            started = false;
        }

    }

    private LifecycleManager getLifecycleManager(String name) throws LifecycleException {
        if(lifecycleManagerMap.containsKey(name)){
            LifecycleManager lifecycleManager = lifecycleManagerMap.get(name);

            return lifecycleManager;

        }else{
            String message = String.format("Unable to locate %s lifecycle manager!",name);
            throw new LifecycleException(message);
        }

    }
}

