package org.molecule.mods.ishell;

import org.molecule.system.Param;

import java.util.Stack;
import java.util.function.Function;

class GetPresentWorkingDomainFunction implements Function<Param, Param> {
    private Stack<String> domainStack;
    public GetPresentWorkingDomainFunction(Stack<String> domainStack) {
        this.domainStack = domainStack;
    }

    @Override
    public Param apply(Param param) {

        String presentWorkingDomain = JLineInteractiveShell.getPrompt(domainStack,"");

        System.out.println(presentWorkingDomain);

        return param;
    }
}
