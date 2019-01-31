package org.molecule.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static org.molecule.util.JSONUtils.merge;

@Slf4j
public class InputStreamConfigurationSource implements ConfigurationSource{

    private JsonNode rootNode;
    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
                    merge(newNode,configRootNode,throwErrorOnMismatchedTypes);
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
        //return jsonNode.isNull();
        //return jsonNode != null;
    }
}
