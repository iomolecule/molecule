package org.molecule.mods.main;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.molecule.system.LifecycleException;
import org.molecule.system.LifecycleManager;
import org.molecule.system.annotations.AsyncEventBus;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutorService;

@Slf4j
class DefaultLifecycleManager implements LifecycleManager{

    private EventBus asyncBus;
    private ExecutorService asyncExecutorService;
    private boolean started;

    @Inject
    DefaultLifecycleManager(@AsyncEventBus EventBus eventBus,
                            @Named("asyncbus-executor")ExecutorService executorService){
        this.asyncBus = eventBus;
        this.asyncExecutorService = executorService;
    }


    @Override
    public void start() throws LifecycleException {
        log.info("Starting default lifecycle behaviour");
        asyncBus.post("starting");
        started = true;
    }

    @Override
    public void stop() {
        if(started){
            log.info("Stopping default lifecycle behaviour");
            asyncBus.post("stopping");
            asyncExecutorService.shutdown();
            started = false;
        }

    }
}
