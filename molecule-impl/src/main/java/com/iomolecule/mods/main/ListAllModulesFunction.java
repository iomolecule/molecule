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

package com.iomolecule.mods.main;

import com.iomolecule.module.ModuleInfo;
import com.iomolecule.module.annotations.ModulesInfo;
import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.annotations.Out;
import com.iomolecule.system.annotations.ParamDecl;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static com.iomolecule.mods.main.ListAllModulesFunction.MODULES;

@Id("function://system/main/listAllModulesFunction")
@Out(
        params = @ParamDecl(
                key = MODULES,
                type = Set.class,
                mandatory = true
        )
)
class ListAllModulesFunction implements Function<Param,Param> {

    static final String MODULES = "modules";

    private Set<ModuleInfo> moduleInfos;

    @Inject
    ListAllModulesFunction(@ModulesInfo Set<ModuleInfo> modules){
        Objects.requireNonNull(modules,"modules");
        moduleInfos = modules;
    }

    @Override
    public Param apply(Param param) {
        return param.plus(MODULES,moduleInfos);
    }
}
