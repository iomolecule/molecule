package org.molecule.system;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

public class SimpleOperation implements Operation{

    private String name;
    private URI fnURI;

    public SimpleOperation(String name){
        this(name,(URI)null);
    }

    public SimpleOperation(String name,URI fnURI){
        checkArgument(name != null && !name.isEmpty(),"Operation name cannot be null or empty!");
        this.name = name;
        this.fnURI = fnURI;
    }

    public SimpleOperation(String name,String uriString){
        this.name = name;
        try {
            this.fnURI = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URI getFunctionURI() {
        return fnURI;
    }

    @Override
    public String toString() {
        return "SimpleOperation{" +
                "name='" + name + '\'' +
                ", fnURI=" + fnURI +
                '}';
    }
}
