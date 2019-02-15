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

package com.iomolecule.config;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This configuration source implementation provides the ability to compose multiple configuration sources in to a single logical source
 */
public class CompositeConfigurationSource implements ConfigurationSource{

    private Set<ConfigurationSource> configurationSources;

    public CompositeConfigurationSource(Set<ConfigurationSource> defaultConfigurationSources,
                                        Set<ConfigurationSource> configurationSources){
        Set<ConfigurationSource> mergedSources = new LinkedHashSet<>();
        mergedSources.addAll(defaultConfigurationSources);
        mergedSources.addAll(configurationSources);
    }

    public CompositeConfigurationSource(Set<ConfigurationSource> configurationSources){
        this.configurationSources = configurationSources;
    }

    @Override
    public <T> T get(String path, Class<T> clz) throws ConfigurationException {
        for (ConfigurationSource configurationSource : configurationSources) {
            if(configurationSource.isValid(path)){
                return configurationSource.get(path,clz);
            }
        }

        throw new ConfigurationNotFoundException(
                String.format("Could not find the configuration for path %s in any of the configured configuration sources %s",path,configurationSources));
    }

    @Override
    public <T> T get(String path, Class<T> clz, T defaultVal) {
        try {
            return get(path,clz);
        } catch (ConfigurationException e) {
            return defaultVal;
        }

    }

    @Override
    public boolean isValid(String path) {
        boolean retVal = false;

        for (ConfigurationSource configurationSource : configurationSources) {
            if(configurationSource.isValid(path)){
                retVal = true;
                break;
            }
        }

        return retVal;
    }
}
