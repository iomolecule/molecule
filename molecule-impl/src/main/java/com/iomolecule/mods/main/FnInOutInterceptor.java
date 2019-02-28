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

import com.iomolecule.commons.Constants;
import com.iomolecule.system.Fn;
import com.iomolecule.system.FnInterceptor;
import com.iomolecule.system.Param;
import com.iomolecule.system.ParamDeclaration;
import com.iomolecule.util.FnUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
class FnInOutInterceptor implements FnInterceptor{

    @Override
    public Param before(Fn fn, Param input) {
        List<ParamDeclaration> inDeclarations = fn.getInDeclarations();
        System.out.println("Before fn "+fn);
        input = FnUtils.verifyInParams(input, inDeclarations);
        return input;
    }

    @Override
    public Param after(Fn fn, Param output) {

        List<ParamDeclaration> outDeclarations = fn.getOutDeclarations();

        output = FnUtils.mapOutParams(output,outDeclarations, Constants.OUT_PARAMS);

        System.out.println("After Fn "+fn);
        System.out.println("Output "+output);

        return output;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
