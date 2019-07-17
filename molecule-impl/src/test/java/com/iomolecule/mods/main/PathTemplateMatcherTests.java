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

import com.iomolecule.util.PathTemplateMatcher;
import org.junit.Test;

import java.util.Map;

public class PathTemplateMatcherTests {

    @Test
    public void testSimplePathTemplateMatching(){
        PathTemplateMatcher templateMatcher = new SimplePathTemplateMatcher();
        Map<String, String> templateVariables = templateMatcher.extractTemplateVariables("cargo.shipments.AWB123456.hawbs.HAWB4567.something.AWB345789.get",
                "cargo.shipments.[shipment].hawbs.[hawb].something.[shipment].get", null);

        System.out.println("Template Variables "+templateVariables);


    }
}
