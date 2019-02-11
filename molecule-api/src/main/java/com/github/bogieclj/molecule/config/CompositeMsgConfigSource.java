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

package com.github.bogieclj.molecule.config;


import java.util.Set;

/**
 * This configuration source implementation provides the ability to compose multiple message configuration sources
 * in to a single logical message configuration source
 */
public class CompositeMsgConfigSource extends CompositeConfigurationSource implements MsgConfigSource{

    public CompositeMsgConfigSource(Set<ConfigurationSource> msgConfigSources) {
        super(msgConfigSources);
    }

}
