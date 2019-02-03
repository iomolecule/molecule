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
