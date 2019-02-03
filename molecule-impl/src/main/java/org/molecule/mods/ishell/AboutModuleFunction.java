package org.molecule.mods.ishell;

import org.molecule.module.ModuleInfo;
import org.molecule.module.annotations.ModulesInfo;
import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.molecule.commons.Constants.IN_PARAMS;

@Id("function://system/ishell/jline/aboutModuleFun")
class AboutModuleFunction implements Function<Param,Param> {

    private Set<ModuleInfo> allModules;

    @Inject
    AboutModuleFunction(@ModulesInfo Set<ModuleInfo> modules){
        allModules = modules;
    }

    @Override
    public Param apply(Param param) {

        if(param.containsKey(IN_PARAMS)){
            List<String> args = (List<String>)param.get(IN_PARAMS);
            if(args.isEmpty()){
                System.out.println("Please provide a valid module name to get information for!");
            }else{
                String moduleName = args.get(0);

                List<ModuleInfo> matchingModules = new ArrayList<>();

                allModules.forEach(moduleInfo -> {
                    if(moduleInfo.getName().equalsIgnoreCase(moduleName)){
                        matchingModules.add(moduleInfo);
                    }
                });

                if(matchingModules.isEmpty()){
                    System.out.println(String.format("No module named %s found installed in the system!",moduleName));
                }else {
                    matchingModules.forEach(moduleInfo -> {
                        System.out.println(String.format("%1$15s : %2$25s", "Name", moduleInfo.getName()));
                        System.out.println(String.format("%1$15s : %2$25s", "Version", moduleInfo.getVersion()));
                        System.out.println(String.format("%1$15s : %2$25s", "Vendor", moduleInfo.getVendor()));
                        printAttributes(moduleInfo.getAttributes());
                    });
                }
            }

        }else{
            System.out.println("Please provide a valid module name to get information for!");
        }
        return null;
    }

    private void printAttributes(Map<String, Object> attributes) {
        if(attributes == null || attributes.isEmpty()){
            return;
        }
        System.out.println(String.format("%1$15s : ","Attributes"));

        attributes.forEach((k,v)->{
            System.out.println(String.format("    %1$15s : %2$25s",k,v));
        });
    }
}
