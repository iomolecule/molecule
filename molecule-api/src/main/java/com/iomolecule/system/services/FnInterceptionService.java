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

package com.iomolecule.system.services;

import com.iomolecule.system.Fn;
import com.iomolecule.system.Param;

public interface FnInterceptionService {

    public void start();

    public void stop();

    public Param interceptBefore(Fn fn,Param in);

    public Param interceptAfter(Fn fn,Param out);
}
