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

package com.github.bogieclj.molecule.playground;

import io.github.classgraph.*;

import java.util.List;

public class TestClassGraph {

    public static void main(String[] args) {
        String pkg = "com.github.bogieclj.molecule.mod.test2";
        String idAnnotation = "com.github.bogieclj.molecule.system.annotations.Id";
        try (ScanResult scanResult =
                     new ClassGraph()
                             .ignoreClassVisibility()
                             //.verbose()                   // Log to stderr
                             .enableClassInfo()
                             .enableAnnotationInfo()
                             .whitelistPackages(pkg)      // Scan com.xyz and subpackages (omit to scan all packages)
                             .scan()) {                   // Start the scan
            for (ClassInfo idClassInfo : scanResult.getClassesWithAnnotation(idAnnotation)) {
                AnnotationInfo idAnnotationInfo = idClassInfo.getAnnotationInfo(idAnnotation);
                List<AnnotationParameterValue> idParamVals = idAnnotationInfo.getParameterValues();
                // @com.xyz.Route has one required parameter
                String id = (String) idParamVals.get(0).getValue();
                System.out.println(idClassInfo.getName() + " is annotated with Id " + id);
            }
        }
    }
}
