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

import com.iomolecule.commons.Constants;
import com.iomolecule.module.ModuleInfo;
import com.iomolecule.module.annotations.ModulesInfo;
import com.iomolecule.system.annotations.FnProvider;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.services.ModuleInfoService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@FnProvider
public class InteractiveShellFunctions {


    private ModuleInfoService moduleInfoService;


    @Inject
    public InteractiveShellFunctions(ModuleInfoService moduleInfoService){
        this.moduleInfoService = moduleInfoService;
    }


    @Id("function://system/ishell/jline/aboutModuleFun")
    public @Named("module-info") ModuleInfo printInfoAboutModule(@Named(Constants.IN_PARAMS) String moduleName){
        ModuleInfo moduleInfo = null;
        if(moduleName == null || moduleName.isEmpty()){
            //System.out.println("Please provide a valid module name to get information for!");
            throw new RuntimeException(String.format("Invalid module name %s specified",moduleName));
        }else{

            if(moduleInfoService.hasModuleWithName(moduleName)){
                moduleInfo = moduleInfoService.getModuleInfo(moduleName);
                /*System.out.println(String.format("%1$15s : %2$25s", "Name", moduleInfo.getName()));
                System.out.println(String.format("%1$15s : %2$25s", "Version", moduleInfo.getVersion()));
                System.out.println(String.format("%1$15s : %2$25s", "Vendor", moduleInfo.getVendor()));
                printAttributes(moduleInfo.getAttributes());
                retVal = true;*/

            }else{
                //System.out.println(String.format("No such module with name %s",moduleName));
                throw new RuntimeException("error-no-such-module-registered");
            }

        }

        return moduleInfo;
    }


    /*private void printAttributes(Map<String, Object> attributes) {
        if(attributes == null || attributes.isEmpty()){
            return;
        }
        System.out.println(String.format("%1$15s : ","Attributes"));

        attributes.forEach((k,v)->{
            System.out.println(String.format("    %1$15s : %2$25s",k,v));
        });
    }*/

}
