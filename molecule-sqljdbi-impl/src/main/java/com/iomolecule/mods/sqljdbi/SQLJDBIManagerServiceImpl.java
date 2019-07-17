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

import com.iomolecule.sql.base.services.SQLDatasourceManagerService;
import com.iomolecule.sql.jdbi.services.SQLJDBIManagerService;
import com.iomolecule.system.annotations.NotifyOnEntry;
import com.iomolecule.system.annotations.NotifyOnExit;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;
import java.util.*;

@Slf4j
class SQLJDBIManagerServiceImpl implements SQLJDBIManagerService{

    private SQLDatasourceManagerService sqlDatasourceManagerService;
    private Map<String,Jdbi> jdbiMap;

    @Inject
    SQLJDBIManagerServiceImpl(SQLDatasourceManagerService sqlDatasourceManagerService){
        this.sqlDatasourceManagerService = sqlDatasourceManagerService;
        jdbiMap = new HashMap<>();
    }

    @NotifyOnEntry("sqljdbimanager-starting")
    @NotifyOnExit("sqljdbimanager-started")
    @Override
    public void start() {
        log.debug("Starting...");

        this.sqlDatasourceManagerService.forEach((name,datasource)->{
            jdbiMap.put(name,Jdbi.create(datasource));
        });
    }

    @NotifyOnEntry("sqljdbimanager-stopping")
    @NotifyOnExit("sqljdbimanager-stopped")
    @Override
    public void stop() {
        log.debug("Stopping...");

        jdbiMap.clear();
    }

    @Override
    public Iterator<String> getNames() {
        Set<String> names = jdbiMap.keySet();
        return names.iterator();
    }

    @Override
    public Optional<Jdbi> getJdbiInstance(String name) {
        Optional<Jdbi> jdbiOptional = Optional.empty();
        if(jdbiMap.containsKey(name)){
            jdbiOptional = Optional.ofNullable(jdbiMap.get(name));
        }
        return jdbiOptional;
    }
}
