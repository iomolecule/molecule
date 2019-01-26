package org.molecule.playground;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Names;
import org.molecule.system.Operation;
import org.molecule.system.Shell;
import org.molecule.system.SimpleOperation;
import org.molecule.system.annotations.DomainOperations;
import org.molecule.system.annotations.Shells;
import org.molecule.system.services.DomainService;

import javax.inject.Singleton;
import java.util.List;

import static org.molecule.util.CollectionUtils.LIST;

public class TestModule extends AbstractModule{


    @Override
    protected void configure() {
        bind(CommandLineShell.class).in(Singleton.class);
        bind(Shell.class).annotatedWith(Names.named("shell://commandline/default")).to(CommandLineShell.class);
        MapBinder<String, Shell> shellMapBinder = MapBinder.newMapBinder(binder(), String.class, Shell.class, Shells.class);
        shellMapBinder.addBinding("shell://commandline/default").to(CommandLineShell.class);
    }

    @ProvidesIntoSet
    @DomainOperations
    public List<Operation> provideDomaiOperations(){
        return LIST(Operation.class,
                new SimpleOperation("testOperation1","function://simple/testFun"),
                new SimpleOperation("domain.testOperation2","function://simple/testFun2"),
                new SimpleOperation("domain.subdomain1.testOperation3","function://simple/testFun3")
        );
    }


}
