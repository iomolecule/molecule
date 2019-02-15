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

import javax.inject.Inject;
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
