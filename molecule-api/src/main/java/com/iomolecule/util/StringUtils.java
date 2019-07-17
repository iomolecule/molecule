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

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;
import java.util.Optional;

public final class StringUtils {

    private StringUtils(){}

    public static String format(String formatText,Map<String,Object> map){
        return StringSubstitutor.replace(formatText,map);
    }

    /**
     * Separates a path with dots into 2 parts the first and the rest
     * @param path
     * @return
     */
    public static String[] splitPath(String path){
        if(org.apache.commons.lang3.StringUtils.isEmpty(path)){
            return new String[0];
        }

        int first = path.indexOf('.');

        if(first >=0 ){
            String firstPart = path.substring(0,first);
            if(path.length() > (first+1)) {
                String restPart = path.substring(first + 1, path.length());
                return new String[]{firstPart,restPart};
            }else{
                return new String[]{firstPart};
            }
        }else{
            return new String[]{path};
        }
    }

    public static Optional<String> getTemplateVariable(String text, String start, String end){
        String variableName = null;

        if(!org.apache.commons.lang3.StringUtils.isEmpty(text)){

            text = text.trim();

            if(text.startsWith(start) && text.endsWith(end)){
                variableName = text.substring(start.length());
                variableName = variableName.substring(0,(variableName.length() - end.length()));
            }
        }

        return Optional.ofNullable(variableName);
    }

}
