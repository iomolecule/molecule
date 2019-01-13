package org.molecule.system;

import java.net.URI;
import java.util.function.Function;

public interface Fn extends Function<Param,Param> {

    public URI getURI();

}
