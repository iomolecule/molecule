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

import com.iomolecule.system.Fn;
import com.iomolecule.system.FnInterceptor;
import com.iomolecule.system.Ordered;
import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.FnInterceptors;
import com.iomolecule.system.services.FnInterceptionService;
import com.iomolecule.util.OrderComparator;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
class FnInterceptionServiceImpl implements FnInterceptionService{

    private Set<FnInterceptor> fnInterceptors;
    private List<FnInterceptor> orderedFnInterceptors;

    @Inject
    FnInterceptionServiceImpl(@FnInterceptors Set<FnInterceptor> fnInterceptors){
        this.fnInterceptors = fnInterceptors;
    }

    @Override
    public void start() {
        log.debug("Starting...");

        List<Ordered> orderedList = new ArrayList<>();

        orderedList.addAll(fnInterceptors);

        Collections.sort(orderedList,new OrderComparator());

        orderedFnInterceptors = new ArrayList<>();


        orderedList.forEach(ordered -> {
            orderedFnInterceptors.add((FnInterceptor)ordered);
        });


    }

    @Override
    public void stop() {
        log.debug("Stopping...");

        if(orderedFnInterceptors != null){
            orderedFnInterceptors.clear();

        }
        fnInterceptors = null;
        orderedFnInterceptors = null;
    }

    @Override
    public Param interceptBefore(Fn fn, Param in) {

        if(orderedFnInterceptors != null && !orderedFnInterceptors.isEmpty()){
            for (FnInterceptor orderedFnInterceptor : orderedFnInterceptors) {
                in = orderedFnInterceptor.before(fn,in);
            }
        }
        return in;
    }

    @Override
    public Param interceptAfter(Fn fn, Param out) {

        if(orderedFnInterceptors != null && !orderedFnInterceptors.isEmpty()){
            for(int i=orderedFnInterceptors.size()-1;i >= 0;i--){
                out = orderedFnInterceptors.get(i).after(fn,out);
            }
        }

        return out;
    }
}
