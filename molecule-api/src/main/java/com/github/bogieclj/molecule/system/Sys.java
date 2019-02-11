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

package com.github.bogieclj.molecule.system;

/**
 * Abstraction of a System in Molecule.
 *
 */
public interface Sys {

    /**
     * Method invoked by  the caller to start the 'system'
     * Note that the start method could be synchronous (wait for the system and all services to be started before returning) or
     * could be asynchronous (initiate the start and return immediately). This behaviour entirely depends on the {@link com.github.bogieclj.molecule.system.LifecycleManager} implementation
     * configured in the system when the system is being composed using the @see com.github.bogieclj.molecule.mods.main.SysBuilder
     *
     * @throws LifecycleException Exception thrown when errors occur during system startup
     */
    void start() throws LifecycleException;

    /**
     * Retrieves whether the Sys is started or not
     * @return Flag indicating the started status of a Sys
     */
    boolean isStarted();

    /**
     * Issues a stop signal when the Sys needs to be stopped
     * Note that similar to the start() method, the behaviour (synchronous or asynchronous) depends on the
     * {@link com.github.bogieclj.molecule.system.LifecycleManager} instance being used when building the system.
     */
    void stop();
}
