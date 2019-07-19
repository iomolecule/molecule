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
import com.iomolecule.system.Param;
import com.iomolecule.system.Shell;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.services.DomainService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import static com.iomolecule.commons.Constants.IN_PARAMS;

@Id("function://system/ishell/jline/changeDomainFun")
class ChangeDomainFunction implements Function<Param, Param> {

    private Stack<String> domainStack;
    private DomainService domainService;
    private Shell interactiveShell;

    @Inject
    public ChangeDomainFunction(DomainService domainService,
                                @DomainStack Stack<String> domainStack,
                                @Named("shell://interactive/jline") Shell jLineInteractiveShell) {
        this.domainStack = domainStack;
        this.domainService = domainService;
        this.interactiveShell = jLineInteractiveShell;
    }

    @Override
    public Param apply(Param param) {


            if (param.containsKey(IN_PARAMS)) {
                String destinationDomain = (String)param.get(IN_PARAMS);

                if (destinationDomain == null || destinationDomain.isEmpty()) {
                    System.out.println("Please specify the destination domain (or '/' for root)");
                }

                if(destinationDomain.equals("/")){
                    domainStack.clear();
                    domainStack.push(JLineInteractiveShell.ROOT_DOMAIN);
                    interactiveShell.updateState();

                }else if(destinationDomain.equals("..")){
                    if(domainStack.size()>1){
                        domainStack.pop(); //navigate to previous domain till root domain, beyond which it is invalid
                        interactiveShell.updateState();

                    }
                }else{
                    String fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);
                    String fullyQualifiedDomainPath = JLineInteractiveShell.getPrompt(domainStack,"");

                    if (domainService.isValidDomainAt(fullyQualifiedDomain, destinationDomain)) {

                        domainStack.push(destinationDomain);

                        interactiveShell.updateState();
                        //fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);

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
