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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public final class ClasspathScanner {

    public static List<Class> scanForAnnotatedClasses(String annotationName,String... packageNames){
        List<Class> annotatedClassList = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(packageNames)
                .scan()) {

            ClassInfoList annotatedClassInfoList = scanResult.getClassesWithAnnotation(annotationName);
            for (ClassInfo annotatedClassInfo : annotatedClassInfoList) {
                Class<?> annotatedClass = annotatedClassInfo.loadClass();
                annotatedClassList.add(annotatedClass);
            }
        }
        return annotatedClassList;
    }

}
