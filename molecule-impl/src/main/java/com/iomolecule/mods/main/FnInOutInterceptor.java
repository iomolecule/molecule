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
import com.iomolecule.system.*;
import com.iomolecule.system.services.TypeConversionService;
import com.iomolecule.util.FnUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
class FnInOutInterceptor implements FnInterceptor{

    private TypeConversionService conversionService;

    FnInOutInterceptor(TypeConversionService typeConversionService){
        Objects.requireNonNull(typeConversionService,"type conversion service");
        this.conversionService = typeConversionService;
    }

    @Override
    public Param before(Fn fn, Param input) {
        List<ParamDeclaration> inDeclarations = fn.getInDeclarations();
        //System.out.println("Before fn "+fn);
        input = FnUtils.verifyInParams(input, inDeclarations);
        Param output = null;
        try {
            output = FnUtils.convertToTargetTypes(conversionService,input, inDeclarations);
        } catch (TypeConversionException e) {
           throw new RuntimeException(e);
        }
        return output;
    }

    @Override
    public Param after(Fn fn, Param output) {

        List<ParamDeclaration> outDeclarations = fn.getOutDeclarations();

        output = FnUtils.mapOutParams(output,outDeclarations, Constants.OUT_PARAMS);

        //System.out.println("After Fn "+fn);
        //System.out.println("Output "+output);

        return output;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
