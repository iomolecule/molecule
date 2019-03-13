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
import com.iomolecule.system.services.ModuleInfoService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class ModuleInfoServiceImpl implements ModuleInfoService{

    private Set<ModuleInfo> moduleInfos;
    private Map<String,ModuleInfo> moduleInfoMap;

    ModuleInfoServiceImpl(Set<ModuleInfo> moduleInfoSet){
        this.moduleInfos = moduleInfoSet;
    }

    @Override
    public void start() {

        moduleInfoMap = new HashMap<>();
        for (ModuleInfo moduleInfo : moduleInfos) {
            moduleInfoMap.put(moduleInfo.getName(),moduleInfo);
        }

    }

    @Override
    public ModuleInfo getModuleInfo(String name) {
        ModuleInfo moduleInfo = null;

        if(hasModuleWithName(name)){
            moduleInfo = moduleInfoMap.get(name);
        }

        return moduleInfo;
    }

    @Override
    public boolean hasModuleWithName(String name) {
        boolean retVal = false;
        if(moduleInfoMap.containsKey(name)){
            retVal = true;
        }
        return retVal;
    }

    @Override
    public void stop() {
        moduleInfoMap.clear();
        moduleInfoMap = null;
    }
}
