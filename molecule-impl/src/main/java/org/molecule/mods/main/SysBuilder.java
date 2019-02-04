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

package org.molecule.mods.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.molecule.config.ConfigurationSource;
import org.molecule.module.ModuleInfo;
import org.molecule.system.LifecycleManager;
import org.molecule.system.OnExit;
import org.molecule.system.OnStartup;
import org.molecule.system.Sys;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;


public class SysBuilder {

    private ModuleInfo moduleInfo;
    private ConfigurationSource[] configurationSources;
    private Module[] modules;
    private Class<? extends LifecycleManager> lifecycleManagerClazz = DefaultLifecycleManager.class;
    private Object[] eventSinks;
    private String[] args;
    private Class<? extends OnStartup>[] onStartupClasses;
    private Class<? extends OnExit>[] onExitClasses;

    public SysBuilder(){
        this(null);
    }

    public SysBuilder(String[] args){
        this.args = args;
    }


    public SysBuilder withAttributes(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
        return this;
    }

    public SysBuilder withConfigurations(ConfigurationSource... configurationSources) {
        this.configurationSources = configurationSources;
        return this;
    }

    public SysBuilder withModules(Module... modules) {
        this.modules = modules;
        return this;

    }

    public SysBuilder withEventsSinks(Object... sinks) {
        eventSinks = sinks;
        return this;
    }


    public SysBuilder withLifecycleManager(Class<? extends LifecycleManager> lifecycleManagerClass){
        checkArgument(lifecycleManagerClass != null,"lifecycleManagerClass cannot be null or empty!");
        this.lifecycleManagerClazz = lifecycleManagerClass;
        return this;
    }

    public Sys build() {

        List<Module> moduleList = getModules();

        Injector injector = Guice.createInjector(moduleList);



        return new DefaultSys(injector);
    }

    private List<Module> getModules() {
        List<Module> modulesList = new ArrayList<>();

        Module[] defaultModules = getDefaultModules();

        if(modules != null){
            for (Module module : modules) {
                modulesList.add(module);
            }

        }

        if(defaultModules != null){
            for(Module module : defaultModules){
                modulesList.add(module);
            }
        }

        return modulesList;
    }

    private Module[] getDefaultModules() {
        return new Module[]{
                new DefaultMainModule(moduleInfo,
                        configurationSources,
                        lifecycleManagerClazz,
                        eventSinks,
                        args,
                        onStartupClasses,
                        onExitClasses)
        };
    }

    public SysBuilder withOnStartup(Class<? extends OnStartup>... startupClasses) {
        this.onStartupClasses = startupClasses;
        return this;
    }


    public SysBuilder withOnExit(Class<? extends OnExit>... exitMainClasses) {
        this.onExitClasses = exitMainClasses;
        return this;
    }
}
