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

package org.molecule.system.services;

import org.molecule.system.Fn;
import org.molecule.system.LifecycleException;

import java.net.URI;
import java.util.function.Consumer;

public interface FnBus extends Fn{

    public void forEach(Consumer<Fn> fnConsumer);

    public boolean hasFnForURI(URI uri);

    public void start() throws LifecycleException;

    public void stop();

}
