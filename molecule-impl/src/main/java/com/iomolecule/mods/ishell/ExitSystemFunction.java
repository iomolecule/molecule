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

package com.iomolecule.mods.ishell;

import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.annotations.Out;
import com.iomolecule.system.annotations.ParamDecl;

import java.util.function.Function;

import static com.iomolecule.mods.ishell.IShellConstants.EXIT_SYSTEM;

@Id("function://system/ishell/jline/exitSystemFun")
@Out(
        params = @ParamDecl(key = EXIT_SYSTEM,type = Boolean.class, mandatory = true)
)
class ExitSystemFunction implements Function<Param,Param> {


    @Override
    public Param apply(Param param) {
        System.out.println("Bye! Bye!...");
        return param.plus(EXIT_SYSTEM,Boolean.FALSE);
    }
}
