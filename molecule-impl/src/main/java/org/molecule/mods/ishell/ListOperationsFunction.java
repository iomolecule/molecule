package org.molecule.mods.ishell;

import org.molecule.ishell.annotations.DomainStack;
import org.molecule.system.Param;
import org.molecule.system.annotations.Id;
import org.molecule.system.services.DomainService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

@Id("function://system/ishell/jline/listOperationsFun")
class ListOperationsFunction implements Function<Param,Param> {

    private DomainService domainService;
    private Stack<String> domainStack;

    @Inject
    ListOperationsFunction(DomainService domainService,@DomainStack Stack<String> domainStack){
        this.domainService = domainService;
        this.domainStack = domainStack;
    }

    @Override
    public Param apply(Param param) {

        String fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);

        List<String> operationsAt = domainService.getOperationsAt(fullyQualifiedDomain);

        operationsAt.forEach(operation->{
            System.out.println(String.format("%1$20s",operation));
        });

        return param;
    }
}
