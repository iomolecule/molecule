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

package com.iomolecule.system;

import lombok.Value;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

import static com.iomolecule.util.CollectionUtils.MAP;

@Value
public class Event {

    private UUID id;
    private String name;
    private Instant instant;
    private Map<String,Object> attributes;


    public static Event create(String name, AbstractMap.SimpleEntry<String,Object>... entries){
        return new Event(UUID.randomUUID(),name,Instant.now(),MAP(entries));
    }
}
