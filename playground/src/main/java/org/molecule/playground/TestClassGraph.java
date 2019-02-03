package org.molecule.playground;

import io.github.classgraph.*;

import java.util.List;

public class TestClassGraph {

    public static void main(String[] args) {
        String pkg = "org.molecule.mod.test2";
        String idAnnotation = "org.molecule.system.annotations.Id";
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
