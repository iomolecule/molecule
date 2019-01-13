package org.molecule.system.services;

public interface EventsService {


    public void registerEventSinks();

    public void unRegisterEventSinks();

    public boolean hasAnyEventSinks();

    public int getCountOfEventSinks();

    public Object[] getAllEventSinks();

}
