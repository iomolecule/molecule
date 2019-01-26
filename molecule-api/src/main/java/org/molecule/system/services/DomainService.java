package org.molecule.system.services;

import org.molecule.system.Operation;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Consumer;

public interface DomainService {

    public void start();

    public boolean isValidOperation(String path);

    //public void forEach(Consumer<Operation> operationConsumer);

    public Operation getOperation(String path);

    public List<String> getDomainNamesAt(String path);

    public List<String> getOperationsAt(String path);

    public void stop();

    public void print(PrintStream stream);

    public List<Operation> getAllOperations();

}
