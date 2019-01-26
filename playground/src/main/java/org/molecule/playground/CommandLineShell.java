package org.molecule.playground;

import lombok.extern.slf4j.Slf4j;
import org.molecule.system.Operation;
import org.molecule.system.Shell;
import org.molecule.system.services.DomainService;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.List;


@Slf4j
public class CommandLineShell implements Shell{

    private static class Options{

        @CommandLine.Option(names={"-l","--listOfOperations"},description = "List the operations in the domain!")
        boolean listOperations = false;

        @CommandLine.Option(names={"-c","--countOfOperations"},description="Print the count of operations in the domain!")
        boolean countOperations = false;


    }


    private DomainService domainService;

    @Inject
    public CommandLineShell(DomainService domainService){
        this.domainService = domainService;
    }


    @Override
    public void start(String[] args) {

        Options options = CommandLine.populateCommand(new Options(), args);

        log.info("{}",options);

        List<Operation> allOperations = domainService.getAllOperations();

        if(options.listOperations){
            System.out.println("List of Operations...");
            for (Operation operation : allOperations) {
                System.out.println(String.format("Operation : %s, FunctionURI : %s",operation.getName(),operation.getFunctionURI()));
            }
        }

        if(options.countOperations){
            System.out.println("Count of Operations...");
            System.out.println(allOperations.size());
        }


    }

    @Override
    public void stop() {

    }
}
