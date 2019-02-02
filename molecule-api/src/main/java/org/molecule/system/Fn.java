package org.molecule.system;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Fn extends Function<Param,Param> {

    public URI getURI();

    public default List<ParamDeclaration> getOutDeclarations(){
        return new ArrayList<>();
    }

    public default List<ParamDeclaration> getInDeclarations(){
        return new ArrayList<>();
    }
}
