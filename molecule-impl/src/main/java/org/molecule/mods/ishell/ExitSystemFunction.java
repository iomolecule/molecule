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

package org.molecule.mods.ishell;

import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import java.util.function.Function;

import static org.molecule.mods.ishell.IShellConstants.EXIT_SYSTEM;

@Id("function://system/ishell/jline/exitSystemFun")
class ExitSystemFunction implements Function<Param,Param> {


    @Override
    public Param apply(Param param) {
        System.out.println("Bye! Bye!...");
        return param.plus(EXIT_SYSTEM,Boolean.FALSE);
    }
}
