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

import com.iomolecule.system.TypeConversionException;
import com.iomolecule.system.TypeConverter;
import com.iomolecule.system.services.TypeConversionService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Slf4j
class TypeConversionServiceImpl implements TypeConversionService{


    private Map<Class,TypeConverter> converterMap;

    TypeConversionServiceImpl(Map<Class,TypeConverter> typeConverterMap){
        Objects.requireNonNull(typeConverterMap,"type converter map");
        this.converterMap = typeConverterMap;
    }


    @Override
    public Object convert(Object value, Class destType) throws TypeConversionException {
        if(value == null){
            return null;
        }

        if(destType == null){
            return value;
        }

        if(destType.isAssignableFrom(value.getClass())){
            return value;
        }

        if(!converterMap.containsKey(destType)){
            throw new TypeConversionException(String.format("Could not find a suitable converter for %s",destType));
        }

        TypeConverter typeConverter = converterMap.get(destType);

        return typeConverter.convert(value);
    }

}
