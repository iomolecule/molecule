package org.molecule.playground;

import com.google.common.eventbus.Subscribe;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoSet;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.molecule.mods.main.SysBuilder;
import org.molecule.module.ModuleInfo;
import org.molecule.module.annotations.ModulesInfo;
import org.molecule.system.*;
import org.molecule.system.annotations.DomainOperations;
import org.molecule.system.annotations.EventSink;
import picocli.CommandLine;


import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.molecule.util.CollectionUtils.KV;
import static org.molecule.util.CollectionUtils.LIST;
import static org.molecule.util.CollectionUtils.MAP;

public class Main1 {

    public static void main(String[] args) throws LifecycleException {

        //compose a new system
        //with given attributes
        //with given configurations
        //with specified modules
        //with specified event sinks
/*        Sys compositeSystem = new SysBuilder(args)
                .withAttributes(new ModuleInfo("test","1.0","test-vendor",MAP(KV("att1","something"))))
                .withConfigurations(new ConfigurationSource[0])
                .withModules(new Module[0])
                .withEventsSinks(new TestEventSink())
                .withLifecycleManager(SimpleLifecycleManager.class)
                .withOnStartup(StartupMain.class)
                .withOnExit(ExitMain.class)
                .build();*/

        Sys compositeSystem = new SysBuilder(args)
                .withAttributes(new ModuleInfo("test","1.0","test-vendor",MAP(KV("att1","something"))))
                .withLifecycleManager(SimpleLifecycleManager.class)
                .withOnStartup(StartupMain.class)
                .withModules(
                        new TestModule(),
                        new SomeNewModule(),
                        new SomeOtherNewModule(new ModuleInfo("one","1.3.4","x",MAP())),
                        new SomeOtherNewModule(new ModuleInfo("two","1.3.4","x",MAP())),
                        new SomeOtherNewModule(new ModuleInfo("three","1.3.4","x",MAP())))
                .build();

        compositeSystem.start();


        //compositeSystem.stop();

    }
}

@Slf4j
class TestEventSink{

    @Subscribe
    public void subscribeToLifecycleEvents(String event){
      log.info("Received event {}",event);
    }

}

@Slf4j
class AnotherEventSink{

    @Subscribe
    public void subscribeToLifecycleEvents(String event){
        log.info("Received event {}",event);
    }

}

@Slf4j
@Value
class SomeNewEventSink{

    String name;

    @Subscribe
    public void subscription(String event){
        log.info("{} Received Event {}",name,event);
    }
}

@Slf4j
class StartupMain implements OnStartup {

    Set<ModuleInfo> modules;
    Shell shell;

    @Inject
    StartupMain(@ModulesInfo Set<ModuleInfo> moduleInfoSet,CommandLineShell shell)
    {
        this.modules = moduleInfoSet;
        this.shell = shell;
    }

    @Override
    public void onStart(String[] args){


        shell.start(args);

        /*log.info("Starting up System with argumenst ");
        if(args != null && args.length > 0){
            for (String arg : args) {
                log.info(arg);
            }

        }

        log.info("Registed Modules in the system..");

        if(modules!= null && modules.size() > 0){
            for (ModuleInfo module : modules) {
                log.info("Module {}",module.getName());
            }

        }*/
    }
}


@Slf4j
class ExitMain implements OnExit{

    @Override
    public void onExit() {
        log.info("About to exit the system");
    }
}


class SomeNewModule extends AbstractModule {

    @ProvidesIntoSet
    @ModulesInfo
    public ModuleInfo provideModuleInfo(){
        return new ModuleInfo("SomeNewModule","1.2.0","abc-vendor",
                MAP(KV("some-new-attribute","some-value")));
    }

    @ProvidesIntoSet
    @EventSink
    public Object provideEventSink(){
        return new AnotherEventSink();
    }

    @ProvidesIntoSet
    @DomainOperations
    public List<Operation> provideDomaiOperations(){
        return LIST(Operation.class,
                new SimpleOperation("domain2.testOperation2","function://simple/testFun2"),
                new SimpleOperation("domain2.subdomain1.testOperation3","function://simple/testFun3")
        );
    }
}


class SomeOtherNewModule extends AbstractModule{

    private ModuleInfo moduleInfo;

    SomeOtherNewModule(ModuleInfo moduleInfo){
        this.moduleInfo = moduleInfo;
    }

    @ProvidesIntoSet
    @ModulesInfo
    public ModuleInfo provideModuleInfo(){
        return moduleInfo;
    }

    @ProvidesIntoSet
    @EventSink
    public Object provideEventSink(){
        return new SomeNewEventSink(moduleInfo.getName());
    }

    @ProvidesIntoSet
    @DomainOperations
    public List<Operation> provideDomaiOperations(){
        return LIST(Operation.class,
                new SimpleOperation("domain3.testOperation2","function://simple/testFun2"),
                new SimpleOperation("domain3.subdomain1.testOperation3","function://simple/testFun3")
        );
    }
}