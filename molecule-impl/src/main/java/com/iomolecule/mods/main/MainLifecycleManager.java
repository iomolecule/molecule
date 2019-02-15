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

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import com.iomolecule.system.LifecycleException;
import com.iomolecule.system.LifecycleManager;
import com.iomolecule.system.annotations.AsyncEventBus;
import com.iomolecule.system.services.DomainService;
import com.iomolecule.system.services.EventsService;
import com.iomolecule.system.services.FnBus;
import com.iomolecule.system.services.SysLifecycleCallbackService;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
class MainLifecycleManager implements LifecycleManager{

    private EventBus eventBus;
    private EventsService eventSinkRegistrationService;
    private SysLifecycleCallbackService sysLifecycleCallbackService;
    private DomainService domainService;
    private FnBus fnBus;
    boolean started;

    @Inject
    MainLifecycleManager(@AsyncEventBus EventBus eventBus,
                         EventsService eventSinkRegistrationService,
                         SysLifecycleCallbackService sysLifecycleCallbackService,
                         DomainService domainService,FnBus fnBus){
        checkArgument(eventBus != null,"EventBus cannot be null!");
        checkArgument(eventSinkRegistrationService != null,"EventSinkRegistration Service cannot be null!");
        this.eventBus = eventBus;
        this.eventSinkRegistrationService = eventSinkRegistrationService;
        this.sysLifecycleCallbackService = sysLifecycleCallbackService;
        this.domainService = domainService;
        this.fnBus = fnBus;

    }

    @Override
    public void start() throws LifecycleException {

        log.debug("Starting main framework services...");

        //register the event sinks first
        if(eventSinkRegistrationService.hasAnyEventSinks()){
            for (Object eventSink : eventSinkRegistrationService.getAllEventSinks()) {
                log.debug("Registering event sink {}",eventSink);
            }
            eventSinkRegistrationService.registerEventSinks();
        }

        eventBus.post("STARTING_SYS");

        eventBus.post("FNBUS_STARTING");

        fnBus.start();

        eventBus.post("FNBUS_STARTED");

        log.debug("Starting domain service...");

        eventBus.post("DOMAIN_SERVICE_STARTING");

        domainService.start();

        eventBus.post("DOMAIN_SERVICE_STARTED");

        log.debug("Registered Domains..");

        log.debug("Starting lifecycle of services...");



        eventBus.post("STARTED_SYS");

        sysLifecycleCallbackService.invokeAllStartupCallbacks();

        eventBus.post("STARTUP_CALLBACK_COMPLETED");

        started = true;


    }

    @Override
    public void stop() {

        log.debug("Stopping main framework services...");

        if(started) {
            log.debug("Stop lifecycle of services...");


            eventBus.post("STOPPING_SYS");

            if (eventSinkRegistrationService.hasAnyEventSinks()) {
                for (Object eventSink : eventSinkRegistrationService.getAllEventSinks()) {
                    log.debug("UnRegistering event sink {}", eventSink);
                }
                eventSinkRegistrationService.unRegisterEventSinks();
            }

            sysLifecycleCallbackService.invokeAllExitCallbacks();

            domainService.stop();

            fnBus.stop();
            log.debug("Sys Stop completed...");
            started = false;
        }
    }

}
