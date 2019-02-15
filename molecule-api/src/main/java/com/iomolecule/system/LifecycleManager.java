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

package com.iomolecule.system;

/**
 * The LifecycleManager represents concept of a component which is responsible for handling lifecycle of a set of services
 * within the Molecule Framework.
 * In Molecule the LifecycleManager's can be logically composed to form higher level lifecycle manager instances which
 * is used to build on top of existing lifecycle managers provided within Molecule
 */
public interface LifecycleManager {

    /**
     * Intiates the start event of the LifecycleManager
     * @throws LifecycleException The exception thrown when error occur during system startup
     */
    public void start()throws LifecycleException;

    /**
     * Initiates the stop event of the LifecycleManager
     */
    public void stop();
}
