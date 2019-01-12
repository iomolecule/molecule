package org.molecule.mods.main;

import com.google.common.eventbus.EventBus;
import org.molecule.system.services.EventsService;

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
