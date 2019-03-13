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

package com.iomolecule.system;

import com.iomolecule.util.FnUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodFn implements Fn{


    private Object targetObject;
    private Method targetMethod;
    private List<ParamDeclaration> inParamDecl;
    private List<ParamDeclaration> outParamDecl;
    private URI fnURI;

    public MethodFn(Object target, Method method, String uri, List<ParamDeclaration> inParams,ParamDeclaration outParam){
        Objects.requireNonNull(target,"target object");
        Objects.requireNonNull(method, "target method");
        Objects.requireNonNull(uri,"fn uri");
        Objects.requireNonNull(inParams,"input params");
        Objects.requireNonNull(outParam,"output param");

        this.targetObject = target;
        this.targetMethod = method;
        this.fnURI = FnUtils.toURI(uri);
        this.inParamDecl = inParams;
        this.outParamDecl = new ArrayList();
        this.outParamDecl.add(outParam);
    }

    @Override
    public URI getURI() {
        return fnURI;
    }

    @Override
    public List<ParamDeclaration> getOutDeclarations() {
        return outParamDecl;
    }

    @Override
    public List<ParamDeclaration> getInDeclarations() {
        return inParamDecl;
    }

    @Override
    public Param apply(Param param) {

        Object[] args = FnUtils.extractArgs(inParamDecl, param);

        Param outParam = null;
        try {
            Object resultObj = targetMethod.invoke(targetObject, args);
            outParam = FnUtils.fillReturnParam(param, outParamDecl.get(0), resultObj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
            //e.printStackTrace();
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if(cause != null && cause.getMessage() != null){
                throw new RuntimeException(cause.getMessage());
            }else {
                throw new RuntimeException(e.getCause());
            }
            //e.printStackTrace();
        }

        return outParam;
    }
}
