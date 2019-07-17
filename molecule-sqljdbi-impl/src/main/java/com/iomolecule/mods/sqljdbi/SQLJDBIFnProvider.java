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

package com.iomolecule.mods.sqljdbi;

import com.iomolecule.sql.jdbi.services.SQLJDBIManagerService;
import com.iomolecule.system.annotations.Id;

import javax.inject.Named;
import java.util.*;

public class SQLJDBIFnProvider {

    private SQLJDBIManagerService managerService;

    public SQLJDBIFnProvider(SQLJDBIManagerService sqljdbiManagerService){
        this.managerService = sqljdbiManagerService;
    }

    @Id("function://system/jdbi/listAllJDBIsFunction")
    @Named("jdbi-instances")
    public List<Map<String,String>> listJDBIInstances(){
        List<Map<String,String>> instances = new ArrayList<>();

        Iterator<String> names = managerService.getNames();

        while(names.hasNext()){
            String name = names.next();
            Map<String,String> jdbiMap = new HashMap<>();
            jdbiMap.put("name",name);
            instances.add(jdbiMap);
        }
        return instances;
    }
}
