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

package com.github.bogieclj.molecule.mods.main;


import com.google.common.eventbus.EventBus;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import lombok.extern.slf4j.Slf4j;
import com.github.bogieclj.molecule.config.CompositeConfigurationSource;
import com.github.bogieclj.molecule.config.CompositeMsgConfigSource;
import com.github.bogieclj.molecule.config.ConfigurationSource;
import com.github.bogieclj.molecule.config.MsgConfigSource;
import com.github.bogieclj.molecule.config.annotations.ConfigsSource;
import com.github.bogieclj.molecule.config.annotations.DefaultConfigsSource;
import com.github.bogieclj.molecule.config.annotations.MsgConfigsSource;
import com.github.bogieclj.molecule.module.ModuleInfo;
import com.github.bogieclj.molecule.module.MoleculeModule;
import com.github.bogieclj.molecule.module.annotations.ModulesInfo;
import com.github.bogieclj.molecule.system.*;
import com.github.bogieclj.molecule.system.annotations.*;
import com.github.bogieclj.molecule.system.services.DomainService;
import com.github.bogieclj.molecule.system.services.EventsService;
import com.github.bogieclj.molecule.system.services.FnBus;
import com.github.bogieclj.molecule.system.services.SysLifecycleCallbackService;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.github.bogieclj.molecule.util.CollectionUtils.KV;
import static com.github.bogieclj.molecule.util.CollectionUtils.MAP;

@Slf4j
public class DefaultMainModule extends MoleculeModule{

    private ModuleInfo systemInfo;
    private ConfigurationSource[] configSources;
    private Class<? extends LifecycleManager> lifecycleManagerClass;
    private Class<? extends OnStartup>[] startupClazzes;
    private Class<? extends OnExit>[] exitClazzes;
    private Object[] eventSinks;
    private String[] mainArgs;
    private OnStartup[] onStartupInstances;
    private OnExit[] onExitInstances;

    public DefaultMainModule(ModuleInfo moduleInfo,
                             ConfigurationSource[] configurationSources,
                             Class<? extends LifecycleManager> lifecycleManagerClazz,
                             Object[] eventSinks, String[] mainArgs,
                             Class<? extends OnStartup>[] startupclzes,
                             Class<? extends OnExit>[] onexitClzes,OnStartup[] onStartupInstances,OnExit[] onExitInstances) {
        checkArgument(moduleInfo != null,"ModuleInfo cannot be null or empty!");
        checkArgument(lifecycleManagerClazz != null, "Lifecycle Manager class cannot be null or empty!");
        this.systemInfo = moduleInfo;
        configSources = configurationSources;
        lifecycleManagerClass = lifecycleManagerClazz;
        this.eventSinks = eventSinks;
        this.mainArgs = mainArgs;
        this.startupClazzes = startupclzes;
        this.exitClazzes = onexitClzes;
        this.onStartupInstances = onStartupInstances;
        this.onExitInstances = onExitInstances;
    }

    @Override
    protected void configure() {

        bindMainArgs();

        bindLifecycleManager();

        bindEventSinks();

        bindStartupAndExitInstances();

        bindDomainOperations();

        bindConfigsSources();

        bindMsgConfigsSources();

        bindFuns();

        bindFunc();

        bindLifecycleManagers();

        initModule();

        registerFuncs(ListAllFnsFunction.class);
    }

    private void bindLifecycleManagers() {

        MapBinder<String, LifecycleManager> lifecycleManagerMapBinder =
                MapBinder.newMapBinder(binder(), String.class, LifecycleManager.class,LifecycleManagers.class);

        lifecycleManagerMapBinder.addBinding("main").to(MainLifecycleManager.class).in(Singleton.class);

    }

    private void bindMsgConfigsSources() {
        Multibinder<ConfigurationSource> msgConfigSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                MsgConfigsSource.class);
    }

    private void bindFunc() {
        Multibinder<Function<Param,Param>> funcSet = Multibinder.newSetBinder(binder(),new TypeLiteral<Function<Param,Param>>(){},
                Func.class);

    }

    private void bindFuns() {
        Multibinder<Fn> funs = Multibinder.newSetBinder(binder(),new TypeLiteral<Fn>(){},
                Fun.class);

        Multibinder<List<Fn>> funsList = Multibinder.newSetBinder(binder(),new TypeLiteral<List<Fn>>(){},
                Funs.class);

    }

    private void bindConfigsSources() {
        Multibinder<ConfigurationSource> configsSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                ConfigsSource.class);
        Multibinder<ConfigurationSource> defaultConfigsSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                DefaultConfigsSource.class);

    }

    private void bindDomainOperations() {
        Multibinder<List<Operation>> domainOperations = Multibinder.newSetBinder(binder(),new TypeLiteral<List<Operation>>(){},
                DomainOperations.class);
    }

    private void bindStartupAndExitInstances() {
        Multibinder<OnStartup> onStartupMultibinder = Multibinder.newSetBinder(binder(),OnStartup.class);
        if(startupClazzes != null && startupClazzes.length >0){
            for (Class<? extends OnStartup> startupClazz : startupClazzes) {
                onStartupMultibinder.addBinding().to(startupClazz);
            }

        }

        if(onStartupInstances != null && onStartupInstances.length > 0){
            for (OnStartup onStartupInstance : onStartupInstances) {
                onStartupMultibinder.addBinding().toInstance(onStartupInstance);
            }

        }

        Multibinder<OnExit> onExitMultibinder = Multibinder.newSetBinder(binder(),OnExit.class);
        if(exitClazzes != null && exitClazzes.length >0){
            for (Class<? extends OnExit> exitClazz : exitClazzes) {
                onExitMultibinder.addBinding().to(exitClazz);
            }

        }

        if(onExitInstances != null && onExitInstances.length > 0){
            for (OnExit onExitInstance : onExitInstances) {
                onExitMultibinder.addBinding().toInstance(onExitInstance);
            }

        }

  }

    private void bindMainArgs() {
        OptionalBinder optionalBinder = OptionalBinder.newOptionalBinder(binder(), Key.get(String[].class,MainArgs.class));
        if(mainArgs != null){
            optionalBinder.setBinding().toInstance(mainArgs);
        }
    }

    private void bindEventSinks() {
        Multibinder<Object> multibinder = Multibinder.newSetBinder(binder(), Object.class, EventSink.class);
        if(eventSinks != null && eventSinks.length > 0){
            for (Object eventSink : eventSinks) {
                multibinder.addBinding().toInstance(eventSink);
            }

        }
    }

    private void bindLifecycleManager() {
        log.debug("Binding LifecycleManager to {}",lifecycleManagerClass);
        //bind the lifecycle manager class in singleton scope
        bind(LifecycleManager.class).to(lifecycleManagerClass).in(Singleton.class);
    }


    @Provides
    @SyncEventBus
    @Singleton
    public EventBus provideSyncEventBus(){
        return new EventBus("sync-eventbus");
    }

    @Provides
    @AsyncEventBus
    @Singleton
    public EventBus provideAsyncEventBus(@Named("asyncbus-executor") ExecutorService executorService){


        return new com.google.common.eventbus.AsyncEventBus("async-eventbus",executorService);
    }


    @Provides
    @Named("asyncbus-executor")
    @Singleton
    public ExecutorService provideExecutorService(){
        return Executors.newFixedThreadPool(1); //for now only one thread is assigned
    }


    @Provides
    @Singleton
    public EventsService provideEventSinkRegistrationService(@SyncEventBus EventBus syncEventBus,
                                                             @AsyncEventBus EventBus asyncEventBus,
                                                             @EventSink Set<Object> eventSinksSet){

        log.debug("EventSinkSet {}",eventSinksSet);
        return new EventsServiceImpl(syncEventBus,asyncEventBus,eventSinksSet.toArray());
    }

    @Provides
    @Singleton
    public SysLifecycleCallbackService provideSysLifecycleCallbackService(Set<OnStartup> startups,
                                                                          Set<OnExit> exits,
                                                                          @MainArgs Optional<String[]> mainArgs){

        return new SysLifecycleCallbackServiceImpl(startups,exits,mainArgs);
    }

    @Provides
    @Singleton
    public DomainService provideDomain(@DomainOperations Set<List<Operation>> operationsSet){
        log.debug("OperationsSet {}",operationsSet);
        return new DefaultDomainService(operationsSet);
    }

    @Provides
    @Singleton
    public ConfigurationSource provideCompositeConfigSource(
            @DefaultConfigsSource Set<ConfigurationSource> defaultConfigurationSources,
            @ConfigsSource Set<ConfigurationSource> configurationSources){
        return new CompositeConfigurationSource(defaultConfigurationSources,configurationSources);
    }

    @Provides
    @Singleton
    public MsgConfigSource provideMessageConfigSource(@MsgConfigsSource Set<ConfigurationSource> messageConfigSources){
        return new CompositeMsgConfigSource(messageConfigSources);
    }

    @Provides
    @Singleton
    public FnBus provideDefaultFnBus(@Fun Set<Fn> fns,@Funs Set<List<Fn>> fnsList,@Func Set<Function<Param,Param>> functions, @AsyncEventBus EventBus eventBus,
                                     MsgConfigSource msgConfigSource){

        return new DefaultFnBus(fns,fnsList,functions,eventBus,msgConfigSource);
    }




}
