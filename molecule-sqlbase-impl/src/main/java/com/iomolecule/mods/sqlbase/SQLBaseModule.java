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

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.iomolecule.config.ConfigurationException;
import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.config.InputStreamConfigurationSource;
import com.iomolecule.config.annotations.DefaultConfigsSource;
import com.iomolecule.module.MoleculeModule;
import com.iomolecule.sql.base.SQLDatasourceDefMap;
import com.iomolecule.sql.base.services.SQLDatasourceManagerService;
import com.iomolecule.system.LifecycleManager;

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
    public SQLDatasourceManagerService provideSQLDatasourceManagerService(ConfigurationSource configurationSource) throws ConfigurationException {
        SQLDatasourceDefMap datasourceDefMap = null;
        if(configurationSource.isValid("/sql")){
            datasourceDefMap = configurationSource.get("/sql",SQLDatasourceDefMap.class);
        }else{
            datasourceDefMap = SQLDatasourceDefMap.createSQLDatasourceDefMap(new HashMap<>());
        }
        return new SQLDatasourceManagerServiceImpl(datasourceDefMap);
    }

    @ProvidesIntoSet
    @DefaultConfigsSource
    public ConfigurationSource provideSQLBaseConfigSource(){
        return new InputStreamConfigurationSource(false,true,
                getClass().getResourceAsStream(String.format("/config/%s.json",getClass().getName())));
    }
}
