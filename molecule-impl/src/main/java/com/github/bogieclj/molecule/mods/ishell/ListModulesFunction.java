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

package com.github.bogieclj.molecule.mods.ishell;

import com.github.bogieclj.molecule.module.ModuleInfo;
import com.github.bogieclj.molecule.module.annotations.ModulesInfo;
import com.github.bogieclj.molecule.system.Param;
import com.github.bogieclj.molecule.system.annotations.Id;

import javax.inject.Inject;
import java.util.Set;
import java.util.function.Function;

@Id("function://system/ishell/jline/listModulesFun")
class ListModulesFunction implements Function<Param,Param> {


    private Set<ModuleInfo> allModules;

    private static final String formatText = "%1$25s - %2$5s";

    @Inject
    ListModulesFunction(@ModulesInfo Set<ModuleInfo> modules){
        this.allModules = modules;
    }

    @Override
    public Param apply(Param param) {

        if(allModules != null && !allModules.isEmpty()){

            for (ModuleInfo moduleInfo : allModules) {
                System.out.println(getFormattedHelpMessage(moduleInfo));
            }

        }

        return param;
    }

    private String getFormattedHelpMessage(ModuleInfo moduleInfo) {


        return String.format(formatText,moduleInfo.getName(),moduleInfo.getVersion());
    }

}
