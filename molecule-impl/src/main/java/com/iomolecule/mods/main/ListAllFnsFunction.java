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

import lombok.extern.slf4j.Slf4j;
import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.Id;
import com.iomolecule.system.annotations.Out;
import com.iomolecule.system.annotations.ParamDecl;
import com.iomolecule.system.services.FnBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.iomolecule.mods.main.ListAllFnsFunction.LIST_OF_FNS;

@Id("function://system/main/listAllFnsFunction")
@Out(
        params = {
                @ParamDecl(key=LIST_OF_FNS,type = List.class,mandatory = true)
        }
)
@Slf4j
class ListAllFnsFunction implements Function<Param,Param> {

    static final String LIST_OF_FNS = "registered_fns";

    private FnBus fnBus;

    @Inject
    ListAllFnsFunction(FnBus fnBus){
        this.fnBus = fnBus;
    }

    @Override
    public Param apply(Param param) {

        List<String> listOfFns = new ArrayList<>();

        fnBus.forEach(fn->{
            listOfFns.add(fn.getURI().toString());
        });

        param = param.plus(LIST_OF_FNS,listOfFns);

        return param;
    }
}
