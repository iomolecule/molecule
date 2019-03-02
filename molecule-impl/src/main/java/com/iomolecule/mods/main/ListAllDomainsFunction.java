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

package com.iomolecule.mods.main;

import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.annotations.Out;
import com.iomolecule.system.annotations.ParamDecl;
import com.iomolecule.system.services.DomainService;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.iomolecule.mods.main.ListAllDomainsFunction.DOMAINS;

@Id("function://system/main/listAllDomainsFunction")
@Out(
        params = @ParamDecl(
                key = DOMAINS,
                type = Map.class,
                mandatory = true
        )
)
class ListAllDomainsFunction implements Function<Param,Param> {

    static final String DOMAINS = "domains";

    private DomainService domainService;

    @Inject
    ListAllDomainsFunction(DomainService domainService){
        Objects.requireNonNull(domainService,"domainService");
        this.domainService = domainService;
    }

    @Override
    public Param apply(Param param) {

        Map domainTree = domainService.getDomainTree();

        return param.plus(DOMAINS,domainTree);
    }
}
