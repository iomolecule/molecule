package org.molecule.mods.ishell;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Names;
import org.molecule.system.*;
import org.molecule.system.annotations.DomainOperations;
import org.molecule.system.annotations.Fun;
import org.molecule.system.annotations.Funs;
import org.molecule.system.annotations.Shells;
import org.molecule.system.services.DomainService;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.molecule.util.CollectionUtils.LIST;

public class JLineInteractiveShellModule extends AbstractModule{

    @Override
    protected void configure() {
        //install(new TextIOInteractiveShellModule());
        bind(JLineInteractiveShell.class).in(Singleton.class);
        bind(Shell.class).annotatedWith(Names.named("shell://interactive/jline")).to(JLineInteractiveShell.class);
        MapBinder<String, Shell> shellMapBinder = MapBinder.newMapBinder(binder(), String.class, Shell.class, Shells.class);
        shellMapBinder.addBinding("shell://interactive/jline").to(JLineInteractiveShell.class);

    }

    @Provides
    @Named(JLineInteractiveShell.SHELL_STACK)
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
                new SimpleOperation("pwd","function://system/ishell/jline/getPresentWorkingDomainFun","Prints the present working domain")//,
                //new SimpleOperation("exec","function://system/ishell/jline/executeOperationFun","Executes the specified operation withing the current active domain")
        );

    }

    @ProvidesIntoSet
    @Fun
    @Singleton
    public Fn provideHelpFun(DomainService domainService,@Named(JLineInteractiveShell.SHELL_STACK) Stack<String> domainStack){
        return new URIFn("function://system/ishell/jline/listHelpFun",
                new ListHelpFunction(domainService,domainStack));//,
                //Collections.emptyList(),LIST(ParamDeclaration.class,new DefaultParamDeclaration(ListHelpFunction.OUT_HELP_LIST,List.class,true)));
    }

    @ProvidesIntoSet
    @Fun
    @Singleton
    public Fn provideExitFun(){
        return new URIFn("function://system/ishell/jline/exitSystemFun",new ExitSystemFunction());
    }

    @ProvidesIntoSet
    @Fun
    @Singleton
    public Fn provideDomainListingFun(DomainService domainService,@Named(JLineInteractiveShell.SHELL_STACK) Stack<String> domainStack){
        return new URIFn("function://system/ishell/jline/listDomainsFun",new ListCurrentDomainsFunction(domainService,domainStack));
    }

    @ProvidesIntoSet
    @Fun
    @Singleton
    public Fn provideOperationsListingFun(DomainService domainService,@Named(JLineInteractiveShell.SHELL_STACK) Stack<String> domainStack){
        return new URIFn("function://system/ishell/jline/listOperationsFun",new ListOperationsFunction(domainService,domainStack));
    }

    @ProvidesIntoSet
    @Funs
    @Singleton
    public List<Fn> provideJLineShellFunctions(DomainService domainService,@Named(JLineInteractiveShell.SHELL_STACK) Stack<String> domainStack){
        return LIST(Fn.class,
                new URIFn("function://system/ishell/jline/changeDomainFun",new ChangeDomainFunction(domainService,domainStack)),
                new URIFn("function://system/ishell/jline/getPresentWorkingDomainFun",new GetPresentWorkingDomainFunction(domainStack)));
    }
}
