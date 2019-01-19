package org.molecule.system;

import java.util.Map;

public interface Domain {

    public String getName();

    public Domain getChild(String path);

    public Operation getOperation(String name);

    public Map<String,Operation> getOperations();

    public boolean hasChild(String path);

    public boolean hasOperation(String name);

    public Domain getParent();

    public boolean isRoot();

    public boolean isLeaf();

    boolean hasOperation(OperationDef operationDef);
}
