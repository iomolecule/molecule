package org.molecule.util;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public final class StringUtils {

    private StringUtils(){}

    public static String format(String formatText,Map<String,Object> map){
        return StringSubstitutor.replace(formatText,map);
    }
}
