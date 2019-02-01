package org.molecule.mods.ishell;

import org.molecule.system.Param;
import org.molecule.system.services.DomainService;

import java.util.List;
import java.util.Stack;
import java.util.function.Function;

class ListCurrentDomainsFunction implements Function<Param,Param> {

    private DomainService domainService;
    private Stack<String> shellStack;
    ListCurrentDomainsFunction(DomainService service, Stack<String> shellStack){
        this.domainService = service;
        this.shellStack = shellStack;
    }

    @Override
    public Param apply(Param param) {
        String fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(shellStack);
        List<String> domainNamesAt = domainService.getDomainNamesAt(fullyQualifiedDomain);
        domainNamesAt.forEach(domainName->{
            System.out.println(String.format("%1$15s",domainName));
        });
        return param;
    }
}
