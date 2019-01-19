package org.molecule.util;

import org.molecule.system.DefaultDomain;
import org.molecule.system.Domain;
import org.molecule.system.OperationDef;

import java.util.Set;

public class DomainUtils {

    private DomainUtils(){

    }

    public static Domain buildDomain(Set<OperationDef> operationDefs){

        Domain rootDomain = new DefaultDomain("/");

        if(operationDefs != null && operationDefs.size() > 0){

            for (OperationDef operationDef : operationDefs) {
                if(rootDomain.hasOperation(operationDef)){
                    throw new RuntimeException(String.format("OperationDef {} is duplicated which is not allowed!",operationDef));
                }

                //rootDomain.addOperation(operationDef);
            }


        }

        return rootDomain;
    }
}
