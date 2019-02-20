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

package com.iomolecule.sql.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Map;

@Value
public class SQLDatasourceDef {

    private String url;
    private String userId;
    private String password;
    private Map<String,Object> attributes;

    /**
     * A Factory method for creating SQLDatasourceDef instances
     * @param url URL of the datasource
     * @param userId UserId to connect with the datasource
     * @param password password to connect with the datasource
     * @param attributes Other Datasource specific properties as key and value pairs
     * @return A fully constructed SQLDatasourceDef instance
     */
    @JsonCreator
    public static SQLDatasourceDef createSQLDatasourceDef(
            @JsonProperty("url") String url,
            @JsonProperty("userId") String userId,
            @JsonProperty("password") String password,
            @JsonProperty("attributes") Map<String,Object> attributes
    ){
        return new SQLDatasourceDef(url,userId,password,attributes);
    }
}
