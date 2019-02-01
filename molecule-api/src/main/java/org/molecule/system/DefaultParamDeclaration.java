package org.molecule.system;

import lombok.Value;

@Value
public class DefaultParamDeclaration implements ParamDeclaration{

    private String key;
    private Class type;
    private boolean mandatory;

}
