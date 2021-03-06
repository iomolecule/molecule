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

import lombok.Value;
import lombok.experimental.NonFinal;

import java.lang.annotation.Annotation;

/**
 * The Default implementation of {@link ParamDeclaration}
 */
@Value
public class DefaultParamDeclaration implements ParamDeclaration{

    private String key;
    private Class type;
    private boolean mandatory;
    private Object defaultValue;
    private Class<Annotation> category;


    @Override
    public Class<Annotation> getCategory() {
        return category;
    }

    @Override
    public boolean hasDefaultValue(){
        return defaultValue != null;
    }
}
