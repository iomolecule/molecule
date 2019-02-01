package org.molecule.system;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

public class SimpleOperation implements Operation{

    private String name;
    private URI fnURI;
    private String documentation;
    private String simpleName;

    public SimpleOperation(String name){
        this(name,(URI)null,"Not available at present");
    }

    public SimpleOperation(String name,URI fnURI,String doc){
        checkArgument(name != null && !name.isEmpty(),"Operation name cannot be null or empty!");
        this.name = name;
        this.fnURI = fnURI;
        this.documentation = doc;
        String[] splitNames = name.split("\\.");
        if(splitNames != null && splitNames.length > 0){
            simpleName = splitNames[splitNames.length-1];
        }else{
            simpleName = name;
        }
    }

    public SimpleOperation(String name,String uriString,String doc){
        this.name = name;
        this.documentation = doc;
        try {
            this.fnURI = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String[] splitNames = name.split("\\.");
        if(splitNames != null && splitNames.length > 0){
            simpleName = splitNames[splitNames.length-1];
        }else{
            simpleName = name;
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
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String toString() {
        return "SimpleOperation{" +
                "name='" + name + '\'' +
                ", fnURI=" + fnURI +
                '}';
    }
}
