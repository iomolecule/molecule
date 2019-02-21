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

package com.iomolecule.aop.matchers;

import com.google.inject.matcher.AbstractMatcher;

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodNameMatcher extends AbstractMatcher<Method>{

    private String methodName;

    public MethodNameMatcher(String name){
        Objects.requireNonNull(name,"method name");
        this.methodName = name;
    }

    @Override
    public boolean matches(Method method) {
        boolean retVal = false;
        if(method.getName().equals(methodName)){
            retVal = true;
        }
        return retVal;
    }
}
