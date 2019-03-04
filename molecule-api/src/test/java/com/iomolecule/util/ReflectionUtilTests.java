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

package com.iomolecule.util;

import com.iomolecule.system.ParamDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class ReflectionUtilTests {

    @Test
    public void testSimpleMethodReflection(){

        List<ParamDeclaration> addValuesOptionalInputs = ReflectionUtils.getArgumentInfo(this,
                pickMethod(this.getClass(), "addValuesOptionalInputs"));

        ParamDeclaration returnInfo = ReflectionUtils.getReturnInfo(this,pickMethod(this.getClass(),"addValuesOptionalInputs"));

        log.info("Inputs {}",addValuesOptionalInputs);
        log.info("Outputs {}",returnInfo);

    }

    @Test
    public void testSimpleMethodReflectionMandatory(){

        List<ParamDeclaration> addValuesOptionalInputs = ReflectionUtils.getArgumentInfo(this,
                pickMethod(this.getClass(), "addValuesMandatoryInputs"));

        ParamDeclaration returnInfo = ReflectionUtils.getReturnInfo(this,pickMethod(this.getClass(),"addValuesMandatoryInputs"));

        log.info("Inputs {}",addValuesOptionalInputs);
        log.info("Outputs {}",returnInfo);

    }

    @Test
    public void testSimpleMethodReflectionMandatoryReturn(){

        List<ParamDeclaration> addValuesOptionalInputs = ReflectionUtils.getArgumentInfo(this,
                pickMethod(this.getClass(), "addValuesMandatoryInputsAndOutput"));

        ParamDeclaration returnInfo = ReflectionUtils.getReturnInfo(this,pickMethod(this.getClass(),"addValuesMandatoryInputsAndOutput"));

        log.info("Inputs {}",addValuesOptionalInputs);
        log.info("Outputs {}",returnInfo);

    }

    @Test
    public void testSimpleMethodReflectionNoAnnotations(){

        List<ParamDeclaration> addValuesOptionalInputs = ReflectionUtils.getArgumentInfo(this,
                pickMethod(this.getClass(), "addValueNoAnnotations"));

        ParamDeclaration returnInfo = ReflectionUtils.getReturnInfo(this,pickMethod(this.getClass(),"addValueNoAnnotations"));

        log.info("Inputs {}",addValuesOptionalInputs);
        log.info("Outputs {}",returnInfo);

    }

    private Method pickMethod(Class<? extends ReflectionUtilTests> aClass, String methodName) {
        Method[] methods = aClass.getMethods();

        Method retVal = null;

        for (Method method : methods) {
            if(method.getName().equalsIgnoreCase(methodName)){
                retVal = method;
                break;
            }
        }

        return retVal;
    }


    //test methods
    public @Named("sum") Integer addValuesOptionalInputs(@Named("val1") Integer a,@Named("val2") Integer b){
        return a + b;
    }

    public @Named("sum") Integer addValuesMandatoryInputs(@Named("val1") @Nonnull Integer a,
                                                          @Named("val2") @Nonnull Integer b){
        return a + b;
    }

    public @Named("mandatory-out") @Nonnull Integer addValuesMandatoryInputsAndOutput(@Named("in1") @Nonnull Integer a,
                                                          @Named("in2") @Nonnull Integer b){
        return a + b;
    }

    public Integer addValueNoAnnotations(@Named("val1") Integer a,String text){
        return 30;
    }


}
