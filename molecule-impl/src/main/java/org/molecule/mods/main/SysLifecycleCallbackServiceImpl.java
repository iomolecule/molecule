package org.molecule.mods.main;

import lombok.extern.slf4j.Slf4j;
import org.molecule.system.OnExit;
import org.molecule.system.OnStartup;
import org.molecule.system.services.SysLifecycleCallbackService;

import java.util.Optional;
import java.util.Set;

@Slf4j
class SysLifecycleCallbackServiceImpl implements SysLifecycleCallbackService{


    private Set<OnStartup> onStartups;
    private Set<OnExit> onExits;
    private Optional<String[]> optionalMainArgs;

    SysLifecycleCallbackServiceImpl(Set<OnStartup> onstartupInstances, Set<OnExit> onExitInstances, Optional<String[]> mainArgs){
        this.onStartups = onstartupInstances;
        this.onExits = onExitInstances;
        this.optionalMainArgs = mainArgs;
    }

    @Override
    public void invokeAllStartupCallbacks() {
      log.info("Invoking all Startup callbacks...{}",onStartups);
      if(onStartups != null && onStartups.size() > 0){
          for (OnStartup onStartup : onStartups) {
              onStartup.onStart(optionalMainArgs.orElse(new String[0]));
          }

      }
    }

    @Override
    public void invokeAllExitCallbacks() {
        log.info("Invoking all Exit callbacks...{}",onExits);
        if(onExits != null && onExits.size() > 0){
            for (OnExit onExit : onExits) {
                onExit.onExit();
            }

        }

    }
}
