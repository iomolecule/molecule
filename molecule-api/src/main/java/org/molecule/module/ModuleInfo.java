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

package org.molecule.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Map;

@Value
public class ModuleInfo {

    private String name;
    private String version;
    private String vendor;
    private Map<String,Object> attributes;


    @JsonCreator
    public static ModuleInfo createModuleInfo(@JsonProperty("name") String name,
                                              @JsonProperty("version") String version,
                                              @JsonProperty("vendor") String vendor,
                                              @JsonProperty("attributes") Map<String,Object> attributes){
        return new ModuleInfo(name,version,vendor,attributes);
    }
}
