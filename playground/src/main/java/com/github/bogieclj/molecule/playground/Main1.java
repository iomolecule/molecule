/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bogieclj.molecule.playground;

import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoSet;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import com.github.bogieclj.molecule.mods.ishell.JLineInteractiveShellModule;
import com.github.bogieclj.molecule.mods.main.SysBuilder;
import com.github.bogieclj.molecule.module.ModuleInfo;
import com.github.bogieclj.molecule.module.annotations.ModulesInfo;
import com.github.bogieclj.molecule.system.*;
import com.github.bogieclj.molecule.system.annotations.DomainOperations;
import com.github.bogieclj.molecule.system.annotations.EventSink;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.github.bogieclj.molecule.util.CollectionUtils.*;

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
                //.withLifecycleManager(SimpleLifecycleManager.class)
                .withOnStartup(StartupMain.class)
                //.withModules(
                //        new JLineInteractiveShellModule())
                .build();


       /* Sys compositeSystem = new SysBuilder(args)
                .withAttributes(new ModuleInfo("simple","1.0","test-vendor",MAP(KV("att1","something"))))
                .build();*/

        compositeSystem.start();


        //compositeSystem.stop();

    }
}

@Slf4j
class TestEventSink{

    @Subscribe
    public void subscribeToLifecycleEvents(String event){
      log.debug("Received event {}",event);
    }

}

@Slf4j
class AnotherEventSink{

    @Subscribe
    public void subscribeToLifecycleEvents(String event){
        log.debug("Received event {}",event);
    }

}

@Slf4j
@Value
class SomeNewEventSink{

    String name;

    @Subscribe
    public void subscription(String event){
        log.debug("{} Received Event {}",name,event);
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

        /*log.debug("Starting up System with argumenst ");
        if(args != null && args.length > 0){
            for (String arg : args) {
                log.debug(arg);
            }

        }

        log.debug("Registed Modules in the system..");

        if(modules!= null && modules.size() > 0){
            for (ModuleInfo module : modules) {
                log.debug("Module {}",module.getName());
            }

        }*/
    }
}


@Slf4j
class ExitMain implements OnExit{

    @Override
    public void onExit() {
        log.debug("About to exit the system");
    }
}


class SomeNewModule extends AbstractModule {


    @Override
    protected void configure() {
        super.configure();
    }

    @ProvidesIntoSet
    @EventSink
    public Object provideEventSink(){
        return new AnotherEventSink();
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
                new SimpleOperation("domain3.testOperation2","function://simple/testFun2",""),
                new SimpleOperation("domain3.subdomain1.testOperation3","function://simple/testFun3","")
        );
    }
}