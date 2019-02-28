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

import com.iomolecule.system.ParamInfo;
import com.iomolecule.system.annotations.Param;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReflectionUtils {


    public static List<ParamInfo> getArgumentInfo(Object object, Method method){
        Objects.requireNonNull(object,"object");
        Objects.requireNonNull(method,"method");

        List<ParamInfo> paramInfos = new ArrayList<>();

        int paramCount = method.getParameterCount();

        if(paramCount > 0){
            Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                ParamInfo paramInfo = getParamInfo(parameter);
                paramInfos.add(paramInfo);
            }

        }

        return paramInfos;
    }

    private static ParamInfo getParamInfo(Parameter parameter) {
        Param annotation = parameter.getAnnotation(Param.class);
        ParamInfo paramInfo = null;
        if(annotation != null){

        }else{
            Nonnull nonNullParam = parameter.getAnnotation(Nonnull.class);
            boolean mandatory = false;
            if(nonNullParam != null){
                mandatory = true;
            }
           // new ParamInfo()
           // paramInfo = new ParamInfo(parameter.getName(),parameter.getType(),)
        }
        return null;
    }

    public static ParamInfo getReturnInfo(Object object,Method method){
        Objects.requireNonNull(object,"object");
        Objects.requireNonNull(method,"method");

        ParamInfo paramInfo = null;


        return paramInfo;
    }
}
