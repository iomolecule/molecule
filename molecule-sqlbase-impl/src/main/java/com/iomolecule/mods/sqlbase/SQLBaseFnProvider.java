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
import com.iomolecule.system.annotations.Id;

import javax.inject.Named;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLBaseFnProvider {

    private SQLDatasourceManagerService managerService;

    SQLBaseFnProvider(SQLDatasourceManagerService managerService){
        this.managerService = managerService;
    }

    @Id("function://system/sqlbase/listAllDatasourcesFunction")
    public @Named("sql-datasources") List<Map<String,String>> listAllDatasources(){
        ArrayList<Map<String,String>> dataSourceList = new ArrayList<>();

        managerService.forEach((dsname,ds)->{
            Map<String,String> dsInfo = new HashMap<>();

            Connection connection = null;

            dsInfo.put("ds-name",dsname);
            try {
                connection = ds.getConnection();
                DatabaseMetaData metaData = connection.getMetaData();
                String productName = metaData.getDatabaseProductName();
                int productVersionMajor = metaData.getDatabaseMajorVersion();
                int productVersionMinor = metaData.getDatabaseMinorVersion();
                int jdbcMajorVersion = metaData.getJDBCMajorVersion();
                int jdbcMinorVersion = metaData.getJDBCMinorVersion();
                String driverName = metaData.getDriverName();
                String driverVersion = metaData.getDriverVersion();
                String userName = metaData.getUserName();

                dsInfo.put("product-name",productName);
                dsInfo.put("product-version",String.format("%d.%d",productVersionMajor,productVersionMinor));
                dsInfo.put("jdbc-version",String.format("%d.%d",jdbcMajorVersion,jdbcMinorVersion));
                dsInfo.put("jdbc-driver-name",driverName);
                dsInfo.put("jdbc-driver-version",driverVersion);
                dsInfo.put("db-username",userName);

            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                if(connection != null){
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            dataSourceList.add(dsInfo);
        });

        return dataSourceList;
    }
}
