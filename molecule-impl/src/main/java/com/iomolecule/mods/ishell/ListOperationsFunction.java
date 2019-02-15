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
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.services.DomainService;

import javax.inject.Inject;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

@Id("function://system/ishell/jline/listOperationsFun")
class ListOperationsFunction implements Function<Param,Param> {

    private DomainService domainService;
    private Stack<String> domainStack;

    @Inject
    ListOperationsFunction(DomainService domainService,@DomainStack Stack<String> domainStack){
        this.domainService = domainService;
        this.domainStack = domainStack;
    }

    @Override
    public Param apply(Param param) {

        String fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);

        List<String> operationsAt = domainService.getOperationsAt(fullyQualifiedDomain);

        operationsAt.forEach(operation->{
            System.out.println(String.format("%1$20s",operation));
        });

        return param;
    }
}
