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
 * Represents the logical shell within in the Molecule framework.
 * Shell is a logical entity which represent the point of interaction between humans and the system built in Molecule.
 * Shell could also logically represent system interaction points such as Http APIs which inturn is used by
 * systems which provide graphical user interfaces.
 */
public interface Shell {

    /**
     * Call back invoked when the Shell is to be started
     * @param args The arguments passed when the system is started.
     */
    public void start(String[] args);


    public void updateState();

    /**
     * The call back invoked when the Shell is to be stopped.
     */
    public void stop();

}
