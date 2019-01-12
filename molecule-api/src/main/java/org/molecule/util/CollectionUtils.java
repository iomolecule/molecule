package org.molecule.util;

import java.util.*;

public class CollectionUtils {

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
