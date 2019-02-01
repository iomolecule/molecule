package org.molecule.system;

public interface ParamDeclaration {

    public String getKey();

    public Class getType();

    public boolean isMandatory();
}
