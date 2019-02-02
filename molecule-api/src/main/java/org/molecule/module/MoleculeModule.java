package org.molecule.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.molecule.system.Param;
import org.molecule.system.annotations.Func;

import javax.inject.Singleton;
import java.util.function.Function;

public abstract class MoleculeModule extends AbstractModule{

    protected void registerFuncs(Class<? extends Function<Param,Param>>... funcs){
        Multibinder<Function<Param,Param>> funcSet = Multibinder.newSetBinder(binder(),new TypeLiteral<Function<Param,Param>>(){},
                Func.class);
        for (Class<? extends Function<Param, Param>> funcClz : funcs) {
            funcSet.addBinding().to(funcClz).in(Singleton.class);
        }
    }
}
