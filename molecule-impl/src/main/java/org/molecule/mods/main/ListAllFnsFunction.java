package org.molecule.mods.main;

import lombok.extern.slf4j.Slf4j;
import org.molecule.system.Fn;
import org.molecule.system.Param;
import org.molecule.system.annotations.Id;
import org.molecule.system.annotations.Out;
import org.molecule.system.annotations.ParamDecl;
import org.molecule.system.services.FnBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.molecule.mods.main.ListAllFnsFunction.LIST_OF_FNS;

@Id("function://system/main/listAllFnsFunction")
@Out(
        params = {
                @ParamDecl(key=LIST_OF_FNS,type = List.class,mandatory = true)
        }
)
@Slf4j
class ListAllFnsFunction implements Function<Param,Param> {

    static final String LIST_OF_FNS = "registered_fns";

    private FnBus fnBus;

    @Inject
    ListAllFnsFunction(FnBus fnBus){
        this.fnBus = fnBus;
    }

    @Override
    public Param apply(Param param) {

        List<String> listOfFns = new ArrayList<>();

        fnBus.forEach(fn->{
            listOfFns.add(fn.getURI().toString());
        });

        param = param.plus(LIST_OF_FNS,listOfFns);

        return param;
    }
}
