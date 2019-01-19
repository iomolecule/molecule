package org.molecule.system;

public interface MutableDomain extends Domain{

    public void addOperation(Operation operation);

    public void addOperation(OperationDef operationDef);

    public void setParent(Domain domain);

}
