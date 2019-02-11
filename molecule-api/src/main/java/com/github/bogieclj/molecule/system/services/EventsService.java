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

package com.github.bogieclj.molecule.system.services;

/**
 * The EventsService takes care of registering and un-registering EventSinks at the startup and shutdown of the molecule system
 * Note: Like all Molecule Services the EventsService's lifecycle is also managed by the respective LifecycleManager
 */
public interface EventsService {


    /**
     * Registers the event sinks in the system to the Async and SyncEventBus
     */
    public void registerEventSinks();

    /**
     * UnRegisters the event sinks in the system from the Async and SyncEventBus instances
     */
    public void unRegisterEventSinks();

    /**
     * Checks if there are any valid event sinks registered in the system
     * @return The availability of subscribers of events
     */
    public boolean hasAnyEventSinks();

    /**
     * Retrieve the cound of subscribers of events
     * @return Count of event subscribers in the system
     */
    public int getCountOfEventSinks();

    /**
     * Retrieve all the subscribers registered in the system
     * @return The array of subscribers
     */
    public Object[] getAllEventSinks();

}
