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

package com.iomolecule.mods.sqlbase;

import com.iomolecule.sql.base.services.SQLDatasourceManagerService;
import com.iomolecule.system.LifecycleException;
import com.iomolecule.system.LifecycleManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
class SQLBaseLifecycleManager implements LifecycleManager{

    private SQLDatasourceManagerService mgrService;

    @Inject
    SQLBaseLifecycleManager(SQLDatasourceManagerService managerService){
        mgrService = managerService;
    }

    @Override
    public void start() throws LifecycleException {
        log.debug("Starting...");

        mgrService.start();
    }

    @Override
    public void stop() {
        log.debug("Stopping...");

        mgrService.stop();
    }


}
