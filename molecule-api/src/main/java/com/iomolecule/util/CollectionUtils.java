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

import java.util.*;

public class CollectionUtils {

    public static <T> AbstractMap.SimpleEntry<String,Class<? extends T>> tuple(String k, Class<? extends T> type){
        return new AbstractMap.SimpleEntry<>(k,type);
    }

    public static AbstractMap.SimpleEntry<String,Object> KV(String k,Object v){
        return new AbstractMap.SimpleEntry<>(k,v);
    }

    public static AbstractMap.SimpleEntry<String,String> attribute(String key, String value){
        return new AbstractMap.SimpleEntry<>(key,value);
    }

    public static Map<String,Object> MAP(AbstractMap.SimpleEntry<String,Object>... entries){
        Map<String,Object> map = new HashMap<>();
        for (AbstractMap.SimpleEntry<String, Object> entry : entries) {
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }

    public static <T> Set<T> SET(Class<T> type,T... ts){
        Set<T> newSet = new HashSet<>();
        newSet.addAll(Arrays.asList(ts));
        return newSet;
    }

    public static <T> List<T> LIST(Class<T> type,T... ts){
        List<T> newList = new ArrayList<>();
        newList.addAll(Arrays.asList(ts));
        return newList;
    }

}
