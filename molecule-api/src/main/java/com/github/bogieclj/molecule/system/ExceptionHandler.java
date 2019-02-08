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
 * ExceptionHandler interface is used within the Molecule framework as an instance to handle all unhandled exceptions in the molecule system.
 *
 */
public interface ExceptionHandler {

    /**
     * Handles the Throwable specified
     * @param throwable The throwable specified which should be handled appropriately
     */
    public void handle(Throwable throwable);

}
