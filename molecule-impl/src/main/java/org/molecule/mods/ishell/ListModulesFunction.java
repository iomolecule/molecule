package org.molecule.mods.ishell;

import org.molecule.module.ModuleInfo;
import org.molecule.module.annotations.ModulesInfo;
import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import javax.inject.Inject;
import java.util.Set;
import java.util.function.Function;

@Id("function://system/ishell/jline/listModulesFun")
class ListModulesFunction implements Function<Param,Param> {


    private Set<ModuleInfo> allModules;

    private static final String formatText = "%1$25s - %2$5s";

    @Inject
    ListModulesFunction(@ModulesInfo Set<ModuleInfo> modules){
        this.allModules = modules;
    }

    @Override
    public Param apply(Param param) {

        if(allModules != null && !allModules.isEmpty()){

            for (ModuleInfo moduleInfo : allModules) {
                System.out.println(getFormattedHelpMessage(moduleInfo));
            }

        }

        return param;
    }

    private String getFormattedHelpMessage(ModuleInfo moduleInfo) {


        return String.format(formatText,moduleInfo.getName(),moduleInfo.getVersion());
    }

}
