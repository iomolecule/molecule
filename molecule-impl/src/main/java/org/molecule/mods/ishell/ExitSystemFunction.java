package org.molecule.mods.ishell;

import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import java.util.function.Function;

import static org.molecule.mods.ishell.IShellConstants.EXIT_SYSTEM;

@Id("function://system/ishell/jline/exitSystemFun")
class ExitSystemFunction implements Function<Param,Param> {


    @Override
    public Param apply(Param param) {
        System.out.println("Bye! Bye!...");
        return param.plus(EXIT_SYSTEM,Boolean.FALSE);
    }
}
