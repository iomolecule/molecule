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

import com.github.bogieclj.molecule.system.Fn;
import com.github.bogieclj.molecule.system.Fn;
import com.github.bogieclj.molecule.system.LifecycleException;

import java.net.URI;
import java.util.function.Consumer;

/**
 * The FnBus is the Function Bus in the system.
 * It maintains the list of all Fn instances registered in the system and allows them to be executed.
 */
public interface FnBus extends Fn {

    /**
     * For each Fn in the bus, the consumer is invoked
     * @param fnConsumer The Consumer Function which should be invoked for each Fn instance in the system
     */
    public void forEach(Consumer<Fn> fnConsumer);

    /**
     * Checks for the presence of an Fn instance for the given URI
     * @param uri The URI of the Fn to look for
     * @return 'True' is present. 'False' if absent
     */
    public boolean hasFnForURI(URI uri);

    /**
     * Starts the FnBus. Calling this method before all the methods in FnBus is necessary to successfully use FnBus
     * @throws LifecycleException The exception when the Lifecycle Event happens
     */
    public void start() throws LifecycleException;

    /**
     * Stops the FnBus and releases resources used if any.
     */
    public void stop();

}
