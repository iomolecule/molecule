package org.molecule.module;

import lombok.Value;

import java.util.Map;

@Value
public class ModuleInfo {

    private String name;
    private String version;
    private String vendor;
    private Map<String,Object> attributes;

}
