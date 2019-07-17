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

import org.junit.Test;

import java.util.Optional;


public class UtilTests {

    @Test
    public void testPathSplitter1(){
        String[] output = StringUtils.splitPath("[awb].hawbs.[hawb].get");

        if(output.length > 1)
            System.out.println(String.format("First %s, Rest %s",output[0],output[1]));
        else if(output.length > 0)
            System.out.println(String.format("Only %s",output[0]));
        else
            System.out.println("Nothing to split");

    }

    @Test
    public void testTemplateVariableExtraction(){
        Optional<String> variableNameOptional = StringUtils.getTemplateVariable("hello","{","}");

        if(variableNameOptional.isPresent()){
            System.out.println("Variable Name -> "+variableNameOptional.get());
        }else{
            System.out.println("No Variable Name extracted");
        }
    }


}
