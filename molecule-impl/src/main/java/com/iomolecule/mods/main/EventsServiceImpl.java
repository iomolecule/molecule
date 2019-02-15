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
import com.iomolecule.system.services.EventsService;

class EventsServiceImpl implements EventsService {

    private EventBus syncEventBus;
    private EventBus asyncEventBus;
    private Object[] eventSinks;

    EventsServiceImpl(EventBus syncEventBus, EventBus asyncEventBus, Object... eventSinks){
        this.syncEventBus = syncEventBus;
        this.asyncEventBus = asyncEventBus;
        this.eventSinks = eventSinks;
    }


    @Override
    public void registerEventSinks() {
        if(hasAnyEventSinks()){
            for (Object eventSink : eventSinks) {
                syncEventBus.register(eventSink);
                asyncEventBus.register(eventSink);
            }
        }
    }

    @Override
    public void unRegisterEventSinks() {
        if(hasAnyEventSinks()){
            for (Object eventSink : eventSinks) {
                syncEventBus.register(eventSink);
                asyncEventBus.register(eventSink);
            }
        }
    }

    @Override
    public boolean hasAnyEventSinks() {
        return eventSinks != null && eventSinks.length > 0;
    }

    @Override
    public int getCountOfEventSinks() {
        return hasAnyEventSinks() ? eventSinks.length : 0;
    }

    @Override
    public Object[] getAllEventSinks() {
        return eventSinks;
    }


}
