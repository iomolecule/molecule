package org.molecule.playground;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.molecule.system.LifecycleException;
import org.molecule.system.LifecycleManager;
import org.molecule.system.annotations.SyncEventBus;
import org.molecule.system.services.DomainService;
import org.molecule.system.services.EventsService;
import org.molecule.system.services.SysLifecycleCallbackService;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class SimpleLifecycleManager implements LifecycleManager{

    private EventBus eventBus;
    private EventsService eventSinkRegistrationService;
    private SysLifecycleCallbackService sysLifecycleCallbackService;
    private DomainService domainService;
    boolean started;

    @Inject
    public SimpleLifecycleManager(@SyncEventBus EventBus eventBus,
                                  EventsService eventSinkRegistrationService,
    SysLifecycleCallbackService sysLifecycleCallbackService,DomainService domainService){
        checkArgument(eventBus != null,"EventBus cannot be null!");
        checkArgument(eventSinkRegistrationService != null,"EventSinkRegistration Service cannot be null!");
        this.eventBus = eventBus;
        this.eventSinkRegistrationService = eventSinkRegistrationService;
        this.sysLifecycleCallbackService = sysLifecycleCallbackService;
        this.domainService = domainService;
    }



    @Override
    public void start() throws LifecycleException {
        //register the event sinks first
        if(eventSinkRegistrationService.hasAnyEventSinks()){
            for (Object eventSink : eventSinkRegistrationService.getAllEventSinks()) {
                log.info("Registering event sink {}",eventSink);
            }


            eventSinkRegistrationService.registerEventSinks();
        }

        log.info("Starting domain service...");
        domainService.start();

        log.info("Registered Domains..");

        log.info("Starting lifecycle of services...");



        eventBus.post("STARTING_SYS");


        eventBus.post("STARTED_SYS");

        sysLifecycleCallbackService.invokeAllStartupCallbacks();

        eventBus.post("STARTUP_CALLBACK_COMPLETED");

        started = true;

    }

    @Override
    public void stop() {

        if(started) {
            log.info("Stop lifecycle of services...");


            eventBus.post("STOPPING_SYS");

            if (eventSinkRegistrationService.hasAnyEventSinks()) {
                for (Object eventSink : eventSinkRegistrationService.getAllEventSinks()) {
                    log.info("UnRegistering event sink {}", eventSink);
                }
                eventSinkRegistrationService.unRegisterEventSinks();
            }

            sysLifecycleCallbackService.invokeAllExitCallbacks();

            log.info("Sys Stop completed...");
            started = false;
        }
    }
}
