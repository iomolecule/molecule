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
import com.iomolecule.sql.base.SQLBaseConfig;
import com.iomolecule.sql.base.SQLDatasourceDef;
import com.iomolecule.sql.base.SQLDatasourceDefMap;
import com.iomolecule.sql.base.services.SQLDatasourceManagerService;
import com.iomolecule.system.Event;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.iomolecule.util.CollectionUtils.KV;

@Slf4j
class SQLDatasourceManagerServiceImpl implements SQLDatasourceManagerService{


    private SQLDatasourceDefMap defMap;
    private SQLBaseConfig sqlBaseConfig;
    private Map<String,HikariDataSource> sqlDatasourceMap;
    private EventBus eventBus;

    public SQLDatasourceManagerServiceImpl(SQLDatasourceDefMap datasourceDefMap, SQLBaseConfig config,EventBus eventBus) {
        defMap = datasourceDefMap;
        sqlBaseConfig = config;
        sqlDatasourceMap = new HashMap<>();
        this.eventBus = eventBus;
    }

    @Override
    public void start() {
        log.debug("Starting...");

        createDatasources();

        log.info("DatasourceDefMap {}",defMap);
    }

    @Override
    public void forEach(BiConsumer<String, DataSource> dataSourceBiConsumer) {
        sqlDatasourceMap.forEach(dataSourceBiConsumer);
    }

    private void createDatasources() {

        if(sqlBaseConfig.getDatasourceCreationStrategy().equalsIgnoreCase("on-startup")){

            log.debug("SQL Datasources will be created on-startup...");

            defMap.getDatasourceMap().forEach((name,datasourceDef)->{
                HikariDataSource ds = createDatasource(datasourceDef);
                sqlDatasourceMap.put(name,ds);
                eventBus.post(Event.create("sql-datasource-created",
                        KV("sql-datasource-def",datasourceDef),KV("datasource-object",ds)));
            });
        }else{
            log.debug("SQL Datasources will be created on-demand...");
        }
    }

    private HikariDataSource createDatasource(SQLDatasourceDef datasourceDef) {

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setUsername(datasourceDef.getUserId());
        hikariConfig.setJdbcUrl(datasourceDef.getUrl());
        hikariConfig.setPassword(datasourceDef.getPassword());
        HikariDataSource ds = new HikariDataSource(hikariConfig);

        return ds;
    }


    @Override
    public void stop() {
        log.debug("Stopping...");

        log.debug("Closing all datasources...");

        if(!sqlDatasourceMap.isEmpty()){
            sqlDatasourceMap.forEach((name,hikariDataSource)->{
               hikariDataSource.close();
               eventBus.post(Event.create("sql-datasource-closed",KV("sql-datasource-name",name),KV("sql-datasource-object",hikariDataSource)));
            });
            sqlDatasourceMap.clear();
        }
    }
}
