package org.molecule.system;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

public class SimpleOperation implements Operation{

    private String name;
    private URI fnURI;
    private String documentation;

    public SimpleOperation(String name){
        this(name,(URI)null,"Not available at present");
    }

    public SimpleOperation(String name,URI fnURI,String doc){
        checkArgument(name != null && !name.isEmpty(),"Operation name cannot be null or empty!");
        this.name = name;
        this.fnURI = fnURI;
        this.documentation = doc;
    }

    public SimpleOperation(String name,String uriString,String doc){
        this.name = name;
        this.documentation = doc;
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
    public String getDoc() {
        return documentation;
    }

    @Override
    public String toString() {
        return "SimpleOperation{" +
                "name='" + name + '\'' +
                ", fnURI=" + fnURI +
                '}';
    }
}
