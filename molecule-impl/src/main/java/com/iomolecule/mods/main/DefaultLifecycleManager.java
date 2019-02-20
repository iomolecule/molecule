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
import com.iomolecule.system.Event;
import com.iomolecule.system.LifecycleException;
import com.iomolecule.system.LifecycleManager;
import com.iomolecule.system.Ordered;
import com.iomolecule.system.annotations.LifecycleManagers;
import com.iomolecule.system.annotations.SyncEventBus;
import com.iomolecule.util.OrderComparator;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static com.iomolecule.util.CollectionUtils.KV;

/**
 * The DefaultLifecycleManager starts and stops the other lifecyclemanager based on their order
 *
 */
@Slf4j
class DefaultLifecycleManager implements LifecycleManager{

    private boolean started;

    private LifecycleManager mainLifecycleManager;
    private Map<String,LifecycleManager> lifecycleManagerMap;
    private List<LifecycleManager> lifecycleManagersOrdered;
    private EventBus eventBus;

    @Inject
    DefaultLifecycleManager(@SyncEventBus EventBus eventBus,@Named("main") LifecycleManager mainLifecycleManager,
            @LifecycleManagers Map<String,LifecycleManager> lifecycleManagerMap){
        this.lifecycleManagerMap = lifecycleManagerMap;
        this.mainLifecycleManager = mainLifecycleManager;
        this.eventBus = eventBus;
    }


    @Override
    public void start() throws LifecycleException {

        lifecycleManagersOrdered = getLifecycleManagerByOrder();

        //first start the main lifecycle manager so that base framework services are started in order
        //getLifecycleManager("main").start();
        startMainLifecycleManager();

        eventBus.post(Event.create("system-initialized"));

        for (LifecycleManager lifecycleManager : lifecycleManagersOrdered) {
            eventBus.post(Event.create("lifecyclemanager-starting",KV("lifecyclemanager",lifecycleManager)));
            lifecycleManager.start();
            eventBus.post(Event.create("lifecyclemanager-started",KV("lifecyclemanager",lifecycleManager)));
        }

        started = true;

        eventBus.post(Event.create("system-started"));
    }

    private void startMainLifecycleManager() throws LifecycleException {
        mainLifecycleManager.start();
    }

    private List<LifecycleManager> getLifecycleManagerByOrder() {

        Collection<LifecycleManager> allLifecycleManagers = lifecycleManagerMap.values();

        List<Ordered> orderedList = new ArrayList<>();

        orderedList.addAll(allLifecycleManagers);

        Collections.sort(orderedList,new OrderComparator());

        List<LifecycleManager> lifecycleManagers = new ArrayList<>();

        orderedList.forEach(ordered -> {
            lifecycleManagers.add((LifecycleManager) ordered);
        });

        return lifecycleManagers;
    }

    @Override
    public void stop() {
        if(started){
            log.debug("Stopping default lifecycle behaviour");
            eventBus.post(Event.create("system-stopping"));



            if(!lifecycleManagersOrdered.isEmpty()){
                //stop the lifecycle managers in the reverse order
                for(int i=(lifecycleManagersOrdered.size()-1);i >= 0;i--){

                    LifecycleManager lifecycleManager = lifecycleManagersOrdered.get(i);

                    eventBus.post(Event.create("lifecyclemanager-stopping",KV("lifecyclemanager",lifecycleManager)));

                    lifecycleManager.stop();

                    eventBus.post(Event.create("lifecyclemanager-stopped",KV("lifecyclemanager",lifecycleManager)));

                }
            }


            //Stop the main lifecycle manager at the end

            //getLifecycleManager("main").stop();
            stopMainLifecycleManager();


            started = false;
            eventBus.post(Event.create("system-stopped"));
        }

    }

    private void stopMainLifecycleManager() {
        mainLifecycleManager.stop();
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

