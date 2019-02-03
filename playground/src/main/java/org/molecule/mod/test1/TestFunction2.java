package org.molecule.mod.test1;

import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import java.util.function.Function;

@Id("function://mod/test2")
class TestFunction2 implements Function<Param,Param> {
    @Override
    public Param apply(Param param) {
        return param;
    }
}
