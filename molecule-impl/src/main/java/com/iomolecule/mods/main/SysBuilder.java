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

package com.iomolecule.mods.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.module.ModuleInfo;
import com.iomolecule.system.*;
import com.iomolecule.system.annotations.FnProvider;
import com.iomolecule.util.ClasspathScanner;

import java.util.*;

import static com.iomolecule.util.CollectionUtils.MAP;
import static com.google.common.base.Preconditions.checkArgument;


public class SysBuilder {

    private ModuleInfo moduleInfo = ModuleInfo.createModuleInfo("molecule","1.0.0","bogie-clj",MAP());
    private ConfigurationSource[] configurationSources;
    private Module[] modules;
    private Class<? extends LifecycleManager> lifecycleManagerClazz = DefaultLifecycleManager.class;
    private Object[] eventSinks;
    private String[] args;
    private Class<? extends OnStartup>[] onStartupClasses;
    private Class<? extends OnExit>[] onExitClasses;
    private OnStartup[] onStartupInstances;
    private OnExit[] onExitInstances;
    private String[] domainDefinitionPaths;
    private String[] classpathsToScanForFnProviders;
    private Map<Class,TypeConverter> typeConverterMap;

    public SysBuilder(){
        this(null);
    }

    public SysBuilder(String[] args){
        this.args = args;
    }

    public SysBuilder withConverterEntries(AbstractMap.SimpleEntry<Class,TypeConverter>... entries){
        typeConverterMap = new HashMap<>();
        for (AbstractMap.SimpleEntry<Class, TypeConverter> entry : entries) {
            typeConverterMap.put(entry.getKey(),entry.getValue());
        }
        return this;
    }

    public SysBuilder withDomainDefintions(String... domainClasspaths){
        this.domainDefinitionPaths = domainClasspaths;
        return this;
    }

    public SysBuilder withFnProviderClasspaths(String... fnProviderClasspaths){
        this.classpathsToScanForFnProviders = fnProviderClasspaths;
        return this;
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

        List<Class> fnProviderClasses = null;

        if(classpathsToScanForFnProviders != null && classpathsToScanForFnProviders.length > 0) {

            fnProviderClasses = ClasspathScanner.scanForAnnotatedClasses(FnProvider.class.getName(), classpathsToScanForFnProviders);

        }

        Module[] defaultModules = getDefaultModules(fnProviderClasses);

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

    private Module[] getDefaultModules(List<Class> fnProviders) {
        return new Module[]{
                new DefaultMainModule(moduleInfo,
                        configurationSources,
                        lifecycleManagerClazz,
                        eventSinks,
                        args,
                        onStartupClasses,
                        onExitClasses,onStartupInstances,onExitInstances,domainDefinitionPaths,fnProviders,typeConverterMap)
        };
    }

    public SysBuilder withOnStartup(Class<? extends OnStartup>... startupClasses) {
        this.onStartupClasses = startupClasses;
        return this;
    }

    public SysBuilder withOnStartupInst(OnStartup... onStartupInstances){
        this.onStartupInstances = onStartupInstances;
        return this;
    }

    public SysBuilder withOnExitInst(OnExit... onExitInstances){
        this.onExitInstances = onExitInstances;
        return this;
    }

    public SysBuilder withOnExit(Class<? extends OnExit>... exitMainClasses) {
        this.onExitClasses = exitMainClasses;
        return this;
    }
}
