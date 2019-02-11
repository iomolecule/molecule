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

/**
 * This interface provides an abstraction of a Configuration Source within molecule framework
 */
public interface ConfigurationSource {

    /**
     * Retrieve the configuration given a path '/' separated and the data type (class) expected of the
     * configuration type
     * @param path A '/' separated path to the configuration
     * @param clz The Class of the configuration information
     * @param <T> The expected data type of the Configuraion information
     * @return The configuration if the path and type is valid.
     * @throws ConfigurationException If the path or type is invalid.
     */
    public <T> T get(String path,Class<T> clz) throws ConfigurationException;

    /**
     * Retrieve the configuration given a path '/' separated and the data type (class) expected of the
     * configuration type. If the configuration is not available the given default will be returned
     * @param path A '/' separated path to the configuration
     * @param clz The Class of the configuration information
     * @param defaultVal The default value to be returned in the absence of the configuration
     * @param <T> The expected data type of the Configuraion information
     * @return The configuration if the path and type is valid. If not the default value is returned
     */
    public <T> T get(String path,Class<T> clz,T defaultVal);

    /**
     * Checks whether the given path is valid within the context of the configuration source
     * @param path A '/' separated path to the configuration
     * @return 'True' if the path is valid, 'False' otherwise
     */
    public boolean isValid(String path);

}
