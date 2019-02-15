/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iomolecule.mods.ishell;

import com.iomolecule.ishell.annotations.DomainStack;
import com.iomolecule.system.Operation;
import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.services.DomainService;

import javax.inject.Inject;
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
