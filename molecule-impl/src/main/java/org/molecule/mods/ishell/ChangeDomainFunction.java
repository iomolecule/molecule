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

import static org.molecule.commons.Constants.IN_PARAMS;

@Id("function://system/ishell/jline/changeDomainFun")
class ChangeDomainFunction implements Function<Param, Param> {

    private Stack<String> domainStack;
    private DomainService domainService;

    @Inject
    public ChangeDomainFunction(DomainService domainService, @DomainStack Stack<String> domainStack) {
        this.domainStack = domainStack;
        this.domainService = domainService;
    }

    @Override
    public Param apply(Param param) {


            if (param.containsKey(IN_PARAMS)) {
                List<String> args = (List<String>) param.get(IN_PARAMS);

                if (args == null || args.isEmpty()) {
                    System.out.println("Please specify the destination domain (or '/' for root)");
                }

                String destinationDomain = args.get(0);

                if(destinationDomain.equals("/")){
                    domainStack.clear();
                    domainStack.push(JLineInteractiveShell.ROOT_DOMAIN);
                }else if(destinationDomain.equals("..")){
                    if(domainStack.size()>1){
                        domainStack.pop(); //navigate to previous domain till root domain, beyond which it is invalid
                    }
                }else{
                    String fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);
                    String fullyQualifiedDomainPath = JLineInteractiveShell.getPrompt(domainStack,"");

                    if (domainService.isValidDomainAt(fullyQualifiedDomain, destinationDomain)) {

                        domainStack.push(destinationDomain);


                        fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);

                    } else {
                        System.out.println(String.format("Domain %s is not a valid domain under %s", destinationDomain, fullyQualifiedDomainPath));
                    }
                }

            } else {
                System.out.println("Please specify the destination domain (or '/' for root)");
            }

        return param;
    }
}
