package org.molecule.mods.ishell;

import org.molecule.ishell.annotations.DomainStack;
import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Stack;
import java.util.function.Function;

@Id("function://system/ishell/jline/getPresentWorkingDomainFun")
class GetPresentWorkingDomainFunction implements Function<Param, Param> {
    private Stack<String> domainStack;

    @Inject
    public GetPresentWorkingDomainFunction(@DomainStack Stack<String> domainStack) {
        this.domainStack = domainStack;
    }

    @Override
    public Param apply(Param param) {

        String presentWorkingDomain = JLineInteractiveShell.getPrompt(domainStack,"");

        System.out.println(presentWorkingDomain);

        return param;
    }
}
