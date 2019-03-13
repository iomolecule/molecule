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

package com.iomolecule.mods.jongo;

import com.iomolecule.mongodb.services.MongoClientService;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoIterable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
class MongoClientServiceImpl implements MongoClientService{

    private MongoClient mongoClient;

    MongoClientServiceImpl(MongoClient mongoClient){
        Objects.requireNonNull(mongoClient,"mongo client");
        this.mongoClient = mongoClient;
    }

    @Override
    public void start() {
        log.debug("Starting...");
        log.debug("Started with mongo-client {}",mongoClient);
    }

    @Override
    public DB getDB(String name) {

        return mongoClient.getDB(name);
    }

    @Override
    public boolean hasDB(String name) {
        return getDBNames().contains(name);
    }

    @Override
    public List<String> getDBNames() {
        MongoIterable<String> mongoDBs = mongoClient.listDatabaseNames();

        List<String> names = new ArrayList<>();

        for (String mongoDB : mongoDBs) {
            names.add(mongoDB);
        }

        return names;
    }

    @Override
    public void stop() {
        log.debug("Stopping...");
        if(mongoClient != null){
            log.debug("Stopping mongo client...");
            mongoClient.close();
            log.debug("Stopped mongo client...");
        }

    }
}
