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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The Fn interface represents a Function in the Molecule Framework.
 * An Fn is uniquely identified by an {@link java.net.URI}.
 * A Fn can provide information about the Input it expects from the caller / invoker.
 * Similarly a Fn can provide information about the Output it provides based on the input from the caller / invoker.
 * The input and output information is optional and required only if the user expects Molecule to automatically validate
 * the presence of input and output
 */
public interface Fn extends Function<Param,Param> {

    /**
     * Retrieve the URI of the Fn
     * @return The URI of the Fn
     */
    public URI getURI();

    /**
     * Retrieve the Output Declarations as a list
     * @return The list of {@link ParamDeclaration} instances which the Fn is expected to provide as output.
     */
    public default List<ParamDeclaration> getOutDeclarations(){
        return new ArrayList<>();
    }

    /**
     * Retrieve the Input declarations as a list
     * @return The list of {@link ParamDeclaration} instances which the Fn expects as input when invoked.
     */
    public default List<ParamDeclaration> getInDeclarations(){
        return new ArrayList<>();
    }



}
