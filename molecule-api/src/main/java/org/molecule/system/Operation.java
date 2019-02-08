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

package org.molecule.system;

import java.net.URI;

/**
 * An Operation represents the operation within the domain.
 */
public interface Operation {

    /**
     * The Name of the operation
     * @return The name of the operation
     */
    public String getName();

    /**
     * Retrieve the Fn URI pointed to by the Operation
     * @return The URI of the Function (fn)
     */
    public URI getFunctionURI();

    /**
     * A brief documentation of the Operation
     * @return The String representing the documentation or help information about the operation
     */
    public String getDoc();

    /**
     * Retrieves the simple name of an Operation
     *
     * @return Retrieves the simple name of the Operation
     */
    public String getSimpleName();

}
