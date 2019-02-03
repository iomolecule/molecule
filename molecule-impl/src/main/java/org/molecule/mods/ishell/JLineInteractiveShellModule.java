package org.molecule.mods.ishell;

import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import org.molecule.ishell.annotations.DomainStack;
import org.molecule.module.MoleculeModule;
import org.molecule.system.Shell;
import org.molecule.system.annotations.Shells;

import javax.inject.Singleton;
import java.util.Stack;

public class JLineInteractiveShellModule extends MoleculeModule{


    @Override
    protected void configure() {
        initModule();
        bind(JLineInteractiveShell.class).in(Singleton.class);
        bind(Shell.class).annotatedWith(Names.named("shell://interactive/jline")).to(JLineInteractiveShell.class);
        MapBinder<String, Shell> shellMapBinder = MapBinder.newMapBinder(binder(), String.class, Shell.class, Shells.class);
        shellMapBinder.addBinding("shell://interactive/jline").to(JLineInteractiveShell.class);
        registerFuncs();
    }

    private void registerFuncs() {

        registerFuncs(ListHelpFunction.class,
                ExitSystemFunction.class,
                ListCurrentDomainsFunction.class,
                ListOperationsFunction.class,
                GetPresentWorkingDomainFunction.class,
                ChangeDomainFunction.class,
                ListModulesFunction.class,
                AboutModuleFunction.class);

    }

    @Provides
    @DomainStack
    @Singleton
    public Stack<String> provideJLineShellStack(){
        return new Stack<String>();
    }

}
