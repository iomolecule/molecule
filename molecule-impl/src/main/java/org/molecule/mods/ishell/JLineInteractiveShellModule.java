package org.molecule.mods.ishell;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Names;
import org.molecule.ishell.annotations.DomainStack;
import org.molecule.module.MoleculeModule;
import org.molecule.system.*;
import org.molecule.system.annotations.*;
import org.molecule.system.services.DomainService;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import static org.molecule.util.CollectionUtils.LIST;

public class JLineInteractiveShellModule extends MoleculeModule{

    @Override
    protected void configure() {
        //install(new TextIOInteractiveShellModule());
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
                ChangeDomainFunction.class);

    }

    @Provides
    @DomainStack
    @Singleton
    public Stack<String> provideJLineShellStack(){
        return new Stack<String>();
    }

    @ProvidesIntoSet
    @DomainOperations
    @Singleton
    public List<Operation> provideSystemOperations(){
        return LIST(Operation.class,
                new SimpleOperation("help","function://system/ishell/jline/listHelpFun","Show this help!"),
                new SimpleOperation("domains","function://system/ishell/jline/listDomainsFun","List all valid accessible domains within the current active domain"),
                new SimpleOperation("ops","function://system/ishell/jline/listOperationsFun","List all valid accessible operations within the current active domain"),
                new SimpleOperation("exit","function://system/ishell/jline/exitSystemFun","Exits the system"),
                new SimpleOperation("cd","function://system/ishell/jline/changeDomainFun","Change to the specified domain"),
                new SimpleOperation("pwd","function://system/ishell/jline/getPresentWorkingDomainFun","Prints the present working domain")
        );

    }

}
