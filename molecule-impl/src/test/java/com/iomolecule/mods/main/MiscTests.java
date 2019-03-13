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

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class MiscTests {

    @Test
    public void stringSplittingTest(){
        String simpleText = "command arg1 arg2";
        String quotedText = "command \"arg1 arg2\" arg3 \"arg4 arg5 arg6\" arg7";


        //Pattern pattern = Pattern.compile(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        //Splitter splitter = Splitter.on(pattern).trimResults().omitEmptyStrings();

        //List<String> simpleSplit = splitter.splitToList(simpleText);
        //List<String> quotedSplit = splitter.splitToList(quotedText);

        //log.info("Simple Split {}",simpleSplit);
        //log.info("Quoted Split {}",quotedSplit);

        String[] simpleSplit = simpleText.split(" ", 2);
        String[] quotedSplit = quotedText.split(" ", 2);

        log.info("Simple --> command : {} args : {}",simpleSplit[0],simpleSplit[1]);
        log.info("Quoted --> command : {} args : {}",quotedSplit[0],quotedSplit[1]);
    }
}
