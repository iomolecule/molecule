package org.molecule.mods.ishell;

import org.molecule.system.Operation;
import org.molecule.system.Param;
import org.molecule.system.services.DomainService;

import java.util.List;
import java.util.Stack;
import java.util.function.Function;

class ListHelpFunction implements Function<Param,Param> {

    static final String OUT_HELP_LIST = "help-list";
    static final int helpMessageWidth = 15;

    private DomainService domainService;
    private Stack<String> domainStack;

    ListHelpFunction(DomainService domainService, Stack<String> domainStack){
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
            System.out.println(getFormattedHelpMessage(helpMessageWidth,operation.getName(),operation.getDoc()));
        });

        //param = param.plus(OUT_HELP_LIST,rootOperations);

        return param;
    }

    private String getFormattedHelpMessage(int helpFormattingWidth, String help, String helpMessage) {
        String formatText = "%1$" + helpFormattingWidth +"s"+" - %2$s";
        return String.format(formatText,help,helpMessage);
    }

}
