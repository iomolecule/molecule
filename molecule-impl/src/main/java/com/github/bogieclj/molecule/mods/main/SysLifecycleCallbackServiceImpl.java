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

import lombok.extern.slf4j.Slf4j;
import com.github.bogieclj.molecule.system.OnExit;
import com.github.bogieclj.molecule.system.OnStartup;
import com.github.bogieclj.molecule.system.services.SysLifecycleCallbackService;

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
      log.debug("Invoking all Startup callbacks...{}",onStartups);
      if(onStartups != null && onStartups.size() > 0){
          for (OnStartup onStartup : onStartups) {
              onStartup.onStart(optionalMainArgs.orElse(new String[0]));
          }

      }
    }

    @Override
    public void invokeAllExitCallbacks() {
        log.debug("Invoking all Exit callbacks...{}",onExits);
        if(onExits != null && onExits.size() > 0){
            for (OnExit onExit : onExits) {
                onExit.onExit();
            }

        }

    }
}
