package org.molecule.playground;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.molecule.system.LifecycleException;
import org.molecule.system.LifecycleManager;
import org.molecule.system.Shell;
import org.molecule.system.annotations.SyncEventBus;
import org.molecule.system.services.DomainService;
import org.molecule.system.services.EventsService;
import org.molecule.system.services.FnBus;
import org.molecule.system.services.SysLifecycleCallbackService;

import javax.inject.Inject;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class SimpleLifecycleManager implements LifecycleManager{

    private EventBus eventBus;
    private EventsService eventSinkRegistrationService;
    private SysLifecycleCallbackService sysLifecycleCallbackService;
    private DomainService domainService;
    private Shell interactiveShell;
    private FnBus fnBus;
    boolean started;

    @Inject
    public SimpleLifecycleManager(@SyncEventBus EventBus eventBus,
                                  EventsService eventSinkRegistrationService,
                                  SysLifecycleCallbackService sysLifecycleCallbackService, DomainService domainService,
                                  @Named("shell://interactive/jline") Shell interactiveShell,FnBus fnBus){
        checkArgument(eventBus != null,"EventBus cannot be null!");
        checkArgument(eventSinkRegistrationService != null,"EventSinkRegistration Service cannot be null!");
        this.eventBus = eventBus;
        this.eventSinkRegistrationService = eventSinkRegistrationService;
        this.sysLifecycleCallbackService = sysLifecycleCallbackService;
        this.domainService = domainService;
        this.interactiveShell = interactiveShell;
        this.fnBus = fnBus;
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

        fnBus.start();

        log.info("Starting domain service...");
        domainService.start();

        log.info("Registered Domains..");

        log.info("Starting lifecycle of services...");



        eventBus.post("STARTING_SYS");


        eventBus.post("STARTED_SYS");

        sysLifecycleCallbackService.invokeAllStartupCallbacks();

        eventBus.post("STARTUP_CALLBACK_COMPLETED");

        started = true;

        log.info("Starting Interactive Shell...");

        interactiveShell.start(new String[0]);

        log.info("Done starting interactive Shell...");

    }

    @Override
    public void stop() {

        if(started) {
            log.info("Stop lifecycle of services...");


            eventBus.post("STOPPING_SYS");

            interactiveShell.stop();

            if (eventSinkRegistrationService.hasAnyEventSinks()) {
                for (Object eventSink : eventSinkRegistrationService.getAllEventSinks()) {
                    log.info("UnRegistering event sink {}", eventSink);
                }
                eventSinkRegistrationService.unRegisterEventSinks();
            }

            sysLifecycleCallbackService.invokeAllExitCallbacks();

            fnBus.stop();
            log.info("Sys Stop completed...");
            started = false;
        }
    }
}
