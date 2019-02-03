package org.molecule.mod.test2;

import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import java.util.function.Function;

@Id("function://mod/test5")
class TestFunction5 implements Function<Param,Param> {
    @Override
    public Param apply(Param param) {
        return param;
    }
}
