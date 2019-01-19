package org.molecule.system;

import java.net.URI;

public class DefaultOperation implements Operation{

    private String name;
    private URI functionURI;

    public DefaultOperation(String name,URI functionURI){
        this.name = name;
        this.functionURI = functionURI;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URI getFunctionURI() {
        return functionURI;
    }


}
