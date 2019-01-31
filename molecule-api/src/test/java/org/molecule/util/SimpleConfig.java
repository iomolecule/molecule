package org.molecule.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleConfig {

    private String testKey;
    private Integer someValue;
    private String decafe = "test-default";
    private String valueUnknown = "someDefault";
}
