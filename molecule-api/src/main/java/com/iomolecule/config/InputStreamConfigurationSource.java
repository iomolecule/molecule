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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iomolecule.util.JSONUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A simple InputStream based ConfigurationSource Implementation.
 * This implementation allows multiple input streams to be composed as a singe configuration source instance.
 *
 * The current implementation only supports 'JSON' streams.
 * Internally the inputstreams are loaded as JSON nodes and they are logically merged in to a singe JSON node tree.
 * The order of merge is as per the order of the input streams.
 */
@Slf4j
public class InputStreamConfigurationSource implements ConfigurationSource{

    private JsonNode rootNode;
    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Constructor for InputStreamConfigurationSource
     * @param allowNullStreams Configuration to specify whether the implementation will allow the passing of null inputstreams
     * @param throwErrorOnMismatchedTypes Configuration to specify whether the error should be thrown on mismatch of data types during merge of input stream content
     * @param inputStreams The array of input streams to be merged
     */
    public InputStreamConfigurationSource(boolean allowNullStreams,boolean throwErrorOnMismatchedTypes,InputStream... inputStreams){

        JsonNode configRootNode = null;

        for (InputStream inputStream : inputStreams) {
            if(!allowNullStreams) {
                checkArgument(inputStream != null, "Configuration Inputstream cannot be null!");
            }
            try {
                JsonNode newNode = OBJECT_MAPPER.readTree(inputStream);
                if(configRootNode == null){
                    configRootNode = newNode;
                }else{
                    JSONUtils.merge(newNode,configRootNode,throwErrorOnMismatchedTypes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        checkArgument(configRootNode != null,"Configuration Source is in an invalid state. No Valid configuration root node found!");

        rootNode = configRootNode;

        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }

    @Override
    public <T> T get(String path, Class<T> clz)throws ConfigurationException {
        if(!isValid(path)){
            throw new ConfigurationNotFoundException(String.format("No valid configuration value found for Path %s",path));
        }

        try {
             T value = OBJECT_MAPPER.treeToValue(rootNode.at(path), clz);
            return value;
        } catch (JsonProcessingException e) {

            throw new ConfigurationTypeException(String.format("Error occurred during type mapping of config path %s to type %s",path,clz),e);
        }
    }


    @Override
    public <T> T get(String path, Class<T> clz, T defaultVal) {
        try {
            T val = get(path, clz);
            return val;
        } catch (ConfigurationException e) {
            return defaultVal;
        }
    }

    @Override
    public boolean isValid(String path) {

        JsonNode jsonNode = rootNode.at(path);
        return !jsonNode.isMissingNode();

    }
}
