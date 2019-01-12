package org.molecule.mods.main;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.molecule.system.LifecycleException;
import org.molecule.system.LifecycleManager;
import org.molecule.system.Sys;


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
            log.info("Stopping Sys...");
            getLifecycleManager().stop();
            started = false;
        }
    }
}
