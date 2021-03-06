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

import com.iomolecule.module.ModuleInfo;
import com.iomolecule.module.annotations.ModulesInfo;
import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.Id;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.iomolecule.commons.Constants.IN_PARAMS;

//@Id("function://system/ishell/jline/aboutModuleFun")
class AboutModuleFunction implements Function<Param,Param> {

    private Set<ModuleInfo> allModules;

    @Inject
    AboutModuleFunction(@ModulesInfo Set<ModuleInfo> modules){
        allModules = modules;
    }

    @Override
    public Param apply(Param param) {

        if(param.containsKey(IN_PARAMS)){
            String moduleName = (String)param.get(IN_PARAMS);
            if(moduleName == null || moduleName.isEmpty()){
                System.out.println("Please provide a valid module name to get information for!");
            }else{
                List<ModuleInfo> matchingModules = new ArrayList<>();

                allModules.forEach(moduleInfo -> {
                    if(moduleInfo.getName().equalsIgnoreCase(moduleName)){
                        matchingModules.add(moduleInfo);
                    }
                });

                if(matchingModules.isEmpty()){
                    System.out.println(String.format("No module named %s found installed in the system!",moduleName));
                }else {
                    matchingModules.forEach(moduleInfo -> {
                        System.out.println(String.format("%1$15s : %2$25s", "Name", moduleInfo.getName()));
                        System.out.println(String.format("%1$15s : %2$25s", "Version", moduleInfo.getVersion()));
                        System.out.println(String.format("%1$15s : %2$25s", "Vendor", moduleInfo.getVendor()));
                        printAttributes(moduleInfo.getAttributes());
                    });
                }
            }

        }else{
            System.out.println("Please provide a valid module name to get information for!");
        }
        return param;
    }

    private void printAttributes(Map<String, Object> attributes) {
        if(attributes == null || attributes.isEmpty()){
            return;
        }
        System.out.println(String.format("%1$15s : ","Attributes"));

        attributes.forEach((k,v)->{
            System.out.println(String.format("    %1$15s : %2$25s",k,v));
        });
    }
}
