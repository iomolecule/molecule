package org.molecule.system;

import java.net.URI;

public interface Operation {

    public String getName();

    public URI getFunctionURI();

    public String getDoc();

    public String getSimpleName();

}
