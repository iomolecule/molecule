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

import com.iomolecule.util.PathTemplateMatcher;
import com.iomolecule.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SimplePathTemplateMatcher implements PathTemplateMatcher {



    @Override
    public Map<String, String> extractTemplateVariables(String path, String templatePath,Map<String,String> templateVariables) {


        Map<String,String> templateVarMap = (templateVariables == null ? new HashMap<>() : templateVariables);

        if(!org.apache.commons.lang3.StringUtils.isEmpty(path)
            && !org.apache.commons.lang3.StringUtils.isEmpty(templatePath)
        ){

            String[] splitPathArray = StringUtils.splitPath(path);
            String[] templatePathArray = StringUtils.splitPath(templatePath);

            if(splitPathArray.length > 0 && templatePathArray.length > 0){
                String name = splitPathArray[0]; //first is always the name
                String templateName = templatePathArray[0];

                Optional<String> templateVarOptional = StringUtils.getTemplateVariable(templateName,
                        OperationNode.TEMPLATE_START, OperationNode.TEMPLATE_END);
                //means this is a template variable and hence can match
                if(isMatching(name,templateName,templateVarOptional)){

                    //update the template variable with the value in the map
                    if(templateVarOptional.isPresent()){
                        String templateVarName = templateVarOptional.get();

                        if(templateVarMap.containsKey(templateVarName)){
                            log.warn("Template Var Name {} with value {} is being replaced with value {}",
                                    templateVarName,templateVarMap.get(templateVarName),name);
                        }

                        templateVarMap.put(templateVarName,name);
                    }

                    if(splitPathArray.length > 1 && templatePathArray.length > 1){

                        String subPath = splitPathArray[1]; //second is the subpath
                        String templateSubPath = templatePathArray[1];

                        return extractTemplateVariables(subPath,templateSubPath,templateVarMap);

                    }
                }
            }


        }


        return templateVarMap;
    }

    private boolean isMatching(String name, String templateName,Optional<String> templateVarOptional) {

        if(templateVarOptional.isPresent()){
            return true; //for now matching all template variables. Later can add support for pattern matching if required
        }

        return name.equalsIgnoreCase(templateName);
    }
}
