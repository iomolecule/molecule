package org.molecule.mod.test1;

import org.molecule.system.Param;
import org.molecule.system.annotations.Id;

import java.util.function.Function;

@Id("function://mod/test1")
class TestFunction1 implements Function<Param,Param> {
    @Override
    public Param apply(Param param) {
        return param;
    }
}
