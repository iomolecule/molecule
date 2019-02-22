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

import com.iomolecule.mods.sqlbase.SQLBaseModule;
import com.iomolecule.module.MoleculeModule;
import com.iomolecule.sql.jdbi.services.SQLJDBIManagerService;
import com.iomolecule.system.LifecycleManager;

import java.util.AbstractMap;

import static com.iomolecule.util.CollectionUtils.tuple;

public class SQLJDBIModule extends MoleculeModule {

    @Override
    protected void configure() {
        super.configure();
        install(new SQLBaseModule());

        registerSingleton(SQLJDBIManagerService.class,SQLJDBIManagerServiceImpl.class);
    }

    @Override
    protected AbstractMap.SimpleEntry<String, Class<? extends LifecycleManager>> getLifecycleManager() {
        return tuple("sqljdbi-lifecyclemanager",SQLJDBILifecycleManager.class);
    }

}
