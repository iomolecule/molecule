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

package com.github.bogieclj.molecule.mods.main;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import com.github.bogieclj.molecule.system.LifecycleException;
import com.github.bogieclj.molecule.system.LifecycleManager;
import com.github.bogieclj.molecule.system.Sys;


import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
class DefaultSys implements Sys{

    private Injector injector;
    private boolean started;

    public DefaultSys(Injector injector) {
        checkArgument(injector != null,"Dependency Injector cannot be null!");
        this.injector = injector;
    }

    @Override
    public void start() throws LifecycleException {
        //look up the lifecycle Manager and start the services

        getLifecycleManager().start();

        registerJVMShutdownHook();

        started = true;

    }

    private void registerJVMShutdownHook() {
        log.debug("Registering JVM Shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                getLifecycleManager().stop();
            }
        },"main-shutdown-hook"));
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    private LifecycleManager getLifecycleManager() {
        return injector.getInstance(LifecycleManager.class);
    }

    @Override
    public void stop() {
        if(started) {
            log.debug("Stopping Sys...");
            getLifecycleManager().stop();
            started = false;
        }
    }
}
