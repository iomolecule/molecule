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

@Id("function://system/ishell/jline/listDomainsFun")
class ListCurrentDomainsFunction implements Function<Param,Param> {

    private DomainService domainService;
    private Stack<String> shellStack;

    @Inject
    ListCurrentDomainsFunction(DomainService service, @DomainStack Stack<String> shellStack){
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
