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
