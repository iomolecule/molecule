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

import com.iomolecule.commons.Constants;
import com.iomolecule.system.DefaultParamDeclaration;
import com.iomolecule.system.ParamDeclaration;
import com.iomolecule.system.TypeConversionException;
import com.iomolecule.system.annotations.DefaultValue;
import com.iomolecule.system.services.TypeConversionService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.*;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ReflectionUtils {


    public static List<Method> getListOfFnProviderMethods(Object object, Class<? extends Annotation> annotationClz){
        Objects.requireNonNull(object,"object");
        Method[] declaredMethods = object.getClass().getDeclaredMethods();

        List<Method> applicableMethods = new ArrayList<>();

        for (Method declaredMethod : declaredMethods) {
            if(declaredMethod.isAnnotationPresent(annotationClz) && (!declaredMethod.getReturnType().equals(Void.TYPE))){
                applicableMethods.add(declaredMethod);
            }
        }

        return applicableMethods;
    }

    public static List<ParamDeclaration> getArgumentInfo(Object object, Method method,TypeConversionService typeConversionService){
        Objects.requireNonNull(object,"object");
        Objects.requireNonNull(method,"method");

        List<ParamDeclaration> paramInfos = new ArrayList<>();

        int paramCount = method.getParameterCount();

        if(paramCount > 0){
            Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                ParamDeclaration paramInfo = getParamInfo(parameter,typeConversionService);
                paramInfos.add(paramInfo);
            }

        }

        return paramInfos;
    }

    private static ParamDeclaration getParamInfo(Parameter parameter,TypeConversionService typeConversionService) {
        Named namedAnnotation = parameter.getAnnotation(Named.class);

        String paramName = parameter.getName();
        boolean mandatory = false;
        Class<?> dataType = null;

        if(namedAnnotation != null){
            //annotation is given precedence
            paramName = namedAnnotation.value();
        }

        Nonnull nonNullParam = parameter.getAnnotation(Nonnull.class);

        if(nonNullParam != null){
            mandatory = true;
        }

        dataType = parameter.getType();

        DefaultValue defaultValueAnnotation = parameter.getAnnotation(DefaultValue.class);

        Object defaultVal = null;

        if(defaultValueAnnotation != null && typeConversionService != null){
            try {
                defaultVal = typeConversionService.convert(defaultValueAnnotation.value(),dataType);
            } catch (TypeConversionException e) {

                log.warn("Failed to convert {} to type {}",defaultValueAnnotation.value(),dataType);
            }
        }

        ParamDeclaration paramInfo = new DefaultParamDeclaration(paramName,dataType,mandatory,defaultVal,null);

        return paramInfo;
    }

    public static ParamDeclaration getReturnInfo(Object object,Method method){
        Objects.requireNonNull(object,"object");
        Objects.requireNonNull(method,"method");

        String paramName = Constants.OUT_PARAMS;
        boolean mandatory = false;
        Class<?> dataType = method.getReturnType();

        Nonnull returnNonNull = method.getAnnotation(Nonnull.class);

        Named namedAnnotation = method.getAnnotation(Named.class);

        if(namedAnnotation != null){
            //annotation is given precedence
            paramName = namedAnnotation.value();
        }

        if(returnNonNull != null){
            mandatory = true;
        }

        ParamDeclaration paramInfo = new DefaultParamDeclaration(paramName,dataType,mandatory,null,null);

        return paramInfo;
    }
}
