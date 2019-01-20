package org.molecule.playground;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import org.molecule.system.Operation;
import org.molecule.system.SimpleOperation;
import org.molecule.system.annotations.DomainOperations;
import org.molecule.system.services.DomainService;

import javax.inject.Singleton;
import java.util.List;

import static org.molecule.util.CollectionUtils.LIST;

public class TestModule extends AbstractModule{


    @Override
    protected void configure() {
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

    @Provides
    @Singleton
    public CommandLineShell provideCommandShell(DomainService domainService){
        return new CommandLineShell(domainService);
    }
}
