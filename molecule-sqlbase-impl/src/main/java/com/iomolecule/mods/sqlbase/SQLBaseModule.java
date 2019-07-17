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

import com.google.common.eventbus.EventBus;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.iomolecule.config.ConfigurationException;
import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.module.MoleculeModule;
import com.iomolecule.sql.base.SQLBaseConfig;
import com.iomolecule.sql.base.SQLDatasourceDefMap;
import com.iomolecule.sql.base.services.SQLDatasourceManagerService;
import com.iomolecule.system.LifecycleManager;
import com.iomolecule.system.annotations.AsyncEventBus;
import com.iomolecule.system.annotations.MethodFnProvider;

import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.HashMap;

import static com.iomolecule.util.CollectionUtils.tuple;

public class SQLBaseModule extends MoleculeModule {

    @Override
    protected AbstractMap.SimpleEntry<String, Class<? extends LifecycleManager>> getLifecycleManager() {
        return tuple("sqlbase-lifecyclemanager",SQLBaseLifecycleManager.class);
    }

    @Provides
    @Singleton
    public SQLDatasourceManagerService provideSQLDatasourceManagerService(ConfigurationSource configurationSource, @AsyncEventBus EventBus eventBus) throws ConfigurationException {
        SQLDatasourceDefMap datasourceDefMap = null;
        if(configurationSource.isValid("/sql")){
            datasourceDefMap = configurationSource.get("/sql",SQLDatasourceDefMap.class);
        }else{
            datasourceDefMap = SQLDatasourceDefMap.createSQLDatasourceDefMap(new HashMap<>());
        }

        SQLBaseConfig sqlBaseConfig = null;

        if(configurationSource.isValid("/sql/config")){
            sqlBaseConfig = configurationSource.get("/sql/config",SQLBaseConfig.class);
        }else{
            sqlBaseConfig = new SQLBaseConfig("on-demand");
        }


        return new SQLDatasourceManagerServiceImpl(datasourceDefMap,sqlBaseConfig,eventBus);
    }

    @ProvidesIntoSet
    @MethodFnProvider
    @Singleton
    public Object provideSQLBaseFnProvider(SQLDatasourceManagerService managerService){
        return new SQLBaseFnProvider(managerService);
    }

}
