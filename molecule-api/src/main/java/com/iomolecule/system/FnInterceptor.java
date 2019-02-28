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
 * FnInterceptor provides a way for molecule modules to define interceptors for Fns
 *
 */
public interface FnInterceptor extends Ordered{

    /**
     * Invoked before the Fn is invoked
     * @param fn The Fn instance on which the interception has to happen
     * @param input The Param input for the Fn
     * @return The Param to be provided as input for the invocation
     */
    public Param before(Fn fn,Param input);

    /**
     * Invoked after the Fn is invoked
     * @param fn The Fn instance on which the interception has to happen
     * @param output The Param instance returned after the Fn invocation
     * @return The Param to be provided as final output after the invocationß
     */
    public Param after(Fn fn,Param output);
}
