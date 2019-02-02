package org.molecule.mods.ishell;

import org.molecule.ishell.annotations.DomainStack;
import org.molecule.system.Operation;
import org.molecule.system.Param;
import org.molecule.system.annotations.Id;
import org.molecule.system.services.DomainService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

@Id("function://system/ishell/jline/listHelpFun")
class ListHelpFunction implements Function<Param,Param> {

    static final String OUT_HELP_LIST = "help-list";
    static final int helpMessageWidth = 15;

    private DomainService domainService;
    private Stack<String> domainStack;

    @Inject
    ListHelpFunction(DomainService domainService, @DomainStack Stack<String> domainStack){
        this.domainService = domainService;
        this.domainStack = domainStack;
    }

    @Override
    public Param apply(Param param) {

        List<Operation> rootOperations = domainService.getAllOperationsAt("");

        String fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);


        if(!fullyQualifiedDomain.isEmpty()) {
            List<Operation> currentDomainOperations = domainService.getAllOperationsAt(fullyQualifiedDomain);

            rootOperations.addAll(currentDomainOperations);
        }


        rootOperations.forEach(operation -> {
            System.out.println(getFormattedHelpMessage(helpMessageWidth,operation.getSimpleName(),operation.getDoc()));
        });


        return param;
    }

    private String getFormattedHelpMessage(int helpFormattingWidth, String help, String helpMessage) {
        String formatText = "%1$" + helpFormattingWidth +"s"+" - %2$s";
        return String.format(formatText,help,helpMessage);
    }

}
